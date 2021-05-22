package com.pmurck.projectMatcher.dao

import com.pmurck.projectMatcher.model.Role
import com.pmurck.projectMatcher.model.*

interface DAO {

    fun createUser(name: String, passwordHash: String, firstName: String, lastName: String): User
    fun getUser(name: String): User?

    fun createOrganization(name: String, adminName: String): Organization
    fun getOrganization(code: String): Organization?

    fun getRolesInOrgsFor(username: String): List<Pair<Role, Organization>> //pensar el Pair

    fun createDeveloper(orgCode: String, username: String, availabilityHours: Int, seniority: Seniority): Developer
    fun getDeveloper(orgCode: String, devId: Int): Developer?
    fun getDeveloper(orgCode: String, username: String): Developer?
    // no priorities, eso va aparte
    fun updateDeveloper(developer: Developer) {
        developer.save()
    }
    fun updateProjectRankingsForDeveloper(orgCode: String, username: String, rankedProjectIds: List<String>)

    fun createPM(orgCode: String, username: String): ProjectManager
    fun getPM(orgCode: String, username: String): ProjectManager?
    fun createProject(orgCode: String, username: String, projectName: String, prePersistModification: (Project) -> Unit): Project
    fun getProject(orgCode: String, username: String, projectId: Int): Project?
    fun getProject(orgCode: String, pmId: Int, projectId: Int): Project?
    // no priorities, eso va aparte
    fun updateProject(project: Project) {
        project.save()
    }
    fun updateDevRankingsForProject(orgCode: String, username: String, projectId: Int, rankedDevIds: List<String>)

    fun getDefaultRequirements(): List<AbstractRequirement>

    fun getProjects(orgCode: String): List<Project>

    fun getActiveDevelopers(orgCode: String): List<Developer>
    fun getActiveProjects(orgCode: String): List<Project>
}