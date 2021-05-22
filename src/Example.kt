package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.Developer
import com.pmurck.projectMatcher.model.Project
import com.pmurck.projectMatcher.model.Seniority

private fun listOfDevIds(vararg devs: Developer): List<String>{
    return devs.map { it.toID().toString() }
}

private fun DAO.updateDevPrioritiesForProject(project: Project, vararg devs: Developer){
    updateDevRankingsForProject(project.pm.org.code, project.pm.user.name, project.idInPm,
        listOfDevIds(*devs))
}

private fun listOfProjIds(vararg projs: Project): List<String>{
    return projs.map { it.toID().toString() }
}

private fun DAO.updateProjectPrioritiesForDeveloper(dev: Developer, vararg projs: Project){
    updateProjectRankingsForDeveloper(dev.org.code, dev.user.name, listOfProjIds(*projs))
}

private fun Project.setRequirements(jrHours: Int, ssrHours: Int, srHours: Int) {
    addRequirement("Senior", setOf(Seniority.SENIOR), srHours)
    addRequirement("Semisenior o superior", setOf(Seniority.SEMISENIOR, Seniority.SENIOR), ssrHours)
    addRequirement("Junior o superior", setOf(Seniority.JUNIOR, Seniority.SEMISENIOR, Seniority.SENIOR), jrHours)
}

fun DAO.initExampleOrg(){
    val adminEjemplo = createUser("javier", "pass", "Javier", "")
    val proyAdminEjemplo = createUser("gonzalo", "pass", "Gonzalo", "")
    val pedro = createUser("pedro", "pass", "Pedro", "")
    val alicia = createUser("alicia", "pass", "Alicia", "")
    val fernando = createUser("fernando", "pass", "Fernando", "")
    val julia = createUser("julia", "pass", "Julia", "")
    val org = createOrganization("Ejemplo Presentación", adminEjemplo.name)
    val pedroDev = createDeveloper(org.code, pedro.name, 40, Seniority.SENIOR)
    val aliciaDev = createDeveloper(org.code, alicia.name, 35, Seniority.SEMISENIOR)
    val fernandoDev = createDeveloper(org.code, fernando.name, 30, Seniority.SEMISENIOR)
    val juliaDev = createDeveloper(org.code, julia.name, 40, Seniority.JUNIOR)
    createPM(org.code, proyAdminEjemplo.name)
    val proyA = createProject(org.code, proyAdminEjemplo.name, "A") { it.setRequirements(10,30,10)}
    val proyB = createProject(org.code, proyAdminEjemplo.name, "B") { it.setRequirements(10,10,20)}
    val proyC = createProject(org.code, proyAdminEjemplo.name, "C") { it.setRequirements(30,15,20)}

    updateDevPrioritiesForProject(proyA, aliciaDev, juliaDev, pedroDev, fernandoDev)
    updateDevPrioritiesForProject(proyB, pedroDev, aliciaDev, fernandoDev, juliaDev)
    updateDevPrioritiesForProject(proyC, aliciaDev, pedroDev, juliaDev, fernandoDev)

    updateProjectPrioritiesForDeveloper(pedroDev, proyC, proyB, proyA)
    updateProjectPrioritiesForDeveloper(aliciaDev, proyA, proyC, proyB)
    updateProjectPrioritiesForDeveloper(fernandoDev, proyA, proyB, proyC)
    updateProjectPrioritiesForDeveloper(juliaDev, proyB, proyC, proyA)
}

fun DAO.initBigExample() {
    val admin = createUser("adrian", "pass", "Adrian", "")
    val org = createOrganization("Modelos 3", admin.name)
    createPM(org.code, admin.name)
    val proyNames = arrayListOf("Tabú Search" to Triple(40,40,40),
        "GRASP" to Triple(40,60,60),
        "Simulated Annealing" to Triple(0, 80, 0),
        "Algoritmos Genéticos" to Triple(40,40,0),
        "Teoria de Juegos" to Triple(0,40,40),
        "Dec. Multi-criterio" to Triple(40,40,0),
        "Prog. Dinamica" to Triple(40,80,40))
    val devNames = arrayListOf("Gabriel" to "", "Susana" to "", "Francisco" to "", "Juan" to "",
        "Mario" to "", "Luis" to "", "Clara" to "", "Sonia" to "",
        "Pablo" to "", "Claudio" to "", "Alejandro" to "", "Daniel" to "",
        "Hernan" to "", "Laura" to "", "Martin" to "", "Guillermo" to "",
        "Mariela" to "", "Miguel" to "", "Jonas" to "", "Mariana" to "")

    val devUsers = devNames.map {
        createUser(
            it.first.toLowerCase() + it.second.toLowerCase(),
            "pass",
            it.first,
            it.second
        )
    }
    val devs = devUsers.map { createDeveloper(org.code, it.name, listOf(30, 35, 40, 45).random(), Seniority.values().random()) }
    val proys = proyNames.map { createProject(org.code, admin.name, it.first) {p -> p.setRequirements(it.second.first, it.second.second, it.second.third)} }

    for (dev in devs) {
        updateProjectPrioritiesForDeveloper(dev, *proys.shuffled().toTypedArray())
    }

    for (proy in proys) {
        updateDevPrioritiesForProject(proy, *devs.shuffled().toTypedArray())
    }
}