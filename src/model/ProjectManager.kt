package com.pmurck.projectMatcher.model

interface ProjectManager {
    val org: Organization
    val user: User
    val idInOrg: Int
    val projects: List<Project>

    //TODO:  fun createProject() aca?
}

