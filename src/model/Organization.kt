package com.pmurck.projectMatcher.model

interface Organization {
    val code: String
    val admin: User
    val name: String
    val devCount: Int
    val pmCount: Int
    val devs: List<Developer>
    val pms: List<ProjectManager>
}

