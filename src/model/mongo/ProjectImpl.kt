package com.pmurck.projectMatcher.model.mongo

import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import com.pmurck.projectMatcher.model.*

data class ProjectImpl(
    val dao: DAOImpl,
    val orgCode: String,
    val pmIdInOrg: Int,
    val embeddedProject: EmbeddedProject,
): Project {

    override val idInPm by embeddedProject::idInPM
    override var name by embeddedProject::name
    override var active by embeddedProject::active

    override val pm: ProjectManager by lazy {
        this.dao.getPM(this.orgCode, this.pmIdInOrg)!!
    }

    // TODO: ojo by lazy: VER combinado con addRequirement
    override val requirements: List<Requirement> by lazy {
        embeddedProject.requirements.map { RequirementImpl(dao, orgCode, pmIdInOrg, this.idInPm, it) }
    }


    data class ID(val projectID: EmbeddedProjectID): Project.ID() {
        override fun toString(): String {
            return projectID.toString()
        }
    }

    private val _id: ID by lazy {
        ID(EmbeddedProjectID(this.pm.org.code, this.pm.idInOrg, this.idInPm))
    }

    override fun toID(): ID {
        return _id
    }

    override fun save() {
        dao.updateProject(this)
    }

    override fun addRequirement(name: String, seniorities: Set<Seniority>, hours: Int) {
        with(embeddedProject.requirements) {
            this.add(EmbeddedRequirement(this.size, name, seniorities.map { it.name }.toSet(), hours))
        }
    }


    // fuerza completitud
    override val rankedDevelopers: List<Developer> by lazy {
        val devs = dao.getDevelopers(this.orgCode).associateBy { it.toID() }.toMutableMap()
        embeddedProject.priorities.map { devs.remove(DeveloperImpl.ID(it))!! }.toMutableList().apply{
            this.addAll(devs.values)
        }
    }

    override val developerRankings: Map<Developer.ID, Int>
        get() {
            return this.rankedDevelopers.mapIndexed { index, developer -> developer.toID() to index+1 }.toMap()
        }


}