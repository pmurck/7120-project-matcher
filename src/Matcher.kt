package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.model.Developer
import com.pmurck.projectMatcher.model.Project
import com.pmurck.projectMatcher.model.Requirement
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.optim.linear.*
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType

class Matcher {

    private val lpVarsByDev = HashMap<Developer.ID, ArrayList<Var>>()
    private val lpVarsByProjReq = HashMap<Requirement.ID, ArrayList<Var>>()
    private val lpVars = ArrayList<Var>()
    private val unassignedDevHours = HashMap<Developer.ID, LPVar>()
    private val unassignedProjReqHours = HashMap<Requirement.ID, LPVar>()

    open class LPVar(val lpIndex: Int, var value: Double? = null)
    class Var(val dev: Developer, val req: Requirement, lpIndex: Int, value: Double? = null): LPVar(lpIndex, value)

    // pensar aparte hash para lpVars para tomar el valor "rapido"

    private val Developer.lpVars: ArrayList<Var>
        get() = this@Matcher.lpVarsByDev.computeIfAbsent(this.toID()) { arrayListOf()}

    private val Requirement.lpVars: ArrayList<Var>
        get() = this@Matcher.lpVarsByProjReq.computeIfAbsent(this.toID()) { arrayListOf()}

    private var Developer.unassignedHoursVar: LPVar
        get() = this@Matcher.unassignedDevHours.getValue(this.toID())
        set(value) {this@Matcher.unassignedDevHours.put(this.toID(), value)}

    private var Requirement.unassignedHoursVar: LPVar
        get() = this@Matcher.unassignedProjReqHours.getValue(this.toID())
        set(value) {this@Matcher.unassignedProjReqHours.put(this.toID(), value)}

    // pasar DAO y definir orgCode
    // o pasar listas y que esten bien armadas
    fun solve(developers: List<Developer>, projects: List<Project>): Unit {
        var lpIndex: Int = 0

        val constraints = ArrayList<LinearConstraint>()

        for (dev in developers) {
            for (project in projects) {
                for (projReq in project.requirements.filter { it.seniorities.contains(dev.seniority) }) {
                    val lpVar = Var(dev, projReq, lpIndex)
                    lpIndex++
                    dev.lpVars.add(lpVar)
                    projReq.lpVars.add(lpVar)
                    lpVars.add(lpVar)
                }
            }
            //aca podria hacerse sum(lpVars) == o <= dev.availability
            // o rehacer abajo con el mismo for
        }

        for (dev in developers) {
            dev.unassignedHoursVar = LPVar(lpIndex)
            lpIndex++
        }

        for (proj in projects) {
            for (projReq in proj.requirements) {
                projReq.unassignedHoursVar = LPVar(lpIndex)
                lpIndex++
            }
        }

        val lpSize = lpIndex

        // iterar el hashmap con vars ? ya tiene todo
        for (dev in developers) {
            addConstraint(constraints, lpSize, dev.lpVars.plus(dev.unassignedHoursVar), dev.availabilityHours.toDouble())
        }

        // iterar el hashmap con vars ? ya tiene todo
        for (proj in projects) {
            for (projReq in proj.requirements) {
                addConstraint(constraints, lpSize, projReq.lpVars.plus(projReq.unassignedHoursVar), projReq.hours.toDouble())
            }
        }

        val objectiveVector = ArrayRealVector(lpSize)
        for (lpVar in lpVars) { //(pesoU*m[Ux, Px] + pesoP*mT[Px, Ux])*horasAsignadas[Ux, Px]
            val DEV_PRIORITY_WEIGHT = 0.5
            val PROJ_PRIORITY_WEIGHT = 0.5
            val projPriorityForDev = with(lpVar.dev.projectRankings) {
                assert(this.size == projects.size)
                1 + this.size - this[lpVar.req.project.toID()]!!
            }
            val devPriorityForProj = with(lpVar.req.project.developerRankings) {
                assert(this.size == developers.size)
                1 + this.size - this[lpVar.dev.toID()]!!
            }

            objectiveVector.setEntry(lpVar.lpIndex, Math.pow(DEV_PRIORITY_WEIGHT*projPriorityForDev + PROJ_PRIORITY_WEIGHT*devPriorityForProj, 2.0)) //desempate
        }

        for (unassignedHoursVar in unassignedProjReqHours.values+unassignedDevHours.values) {
            objectiveVector.setEntry(unassignedHoursVar.lpIndex, - Math.pow((projects.size + developers.size).toDouble(), 2.0)) //desempate
        }

        val objective = LinearObjectiveFunction(objectiveVector, 0.0)
        val constraintSet = LinearConstraintSet(constraints)

        val solver = SimplexSolver()
        val solution = solver.optimize(objective, constraintSet, GoalType.MAXIMIZE, NonNegativeConstraint(true))

        // TODO: TEMP
        for (lpVar in lpVars+unassignedDevHours.values+unassignedProjReqHours.values){
            lpVar.value = solution.pointRef[lpVar.lpIndex]
            if (lpVar is Var) println("X${lpVar.lpIndex}: Dev: ${lpVar.dev.user.name}, Proj: ${lpVar.req.toID()} = ${solution.pointRef[lpVar.lpIndex]}")
        }
    }

    fun addConstraint(constraints: MutableList<LinearConstraint>, constSize: Int, vars: List<LPVar>, rhs: Double) {
        val vector = ArrayRealVector(constSize)

        for (lpVar in vars) {
            vector.setEntry(lpVar.lpIndex, 1.0)
        }
        constraints.add(LinearConstraint(vector, Relationship.EQ, rhs))
    }

    // userId/devId, projectId, reqId
    // TODO: ver pasar orgCode al constructor
    fun assignedHours(developer: Developer, projReq: Requirement): Double? {
        return projReq.lpVars.find { it.dev.org.code == developer.org.code &&
                it.dev.user.name == developer.user.name}?.value
    }

    fun unassignedHours(developer: Developer): Double {
        assert(developer.availabilityHours - developer.lpVars.mapNotNull { it.value }.sum() == developer.unassignedHoursVar.value)
        return developer.unassignedHoursVar.value!!
    }

    fun unassignedHours(projReq: Requirement): Double {
        assert(projReq.hours - projReq.lpVars.mapNotNull { it.value }.sum() == projReq.unassignedHoursVar.value)
        return projReq.unassignedHoursVar.value!!
    }
}