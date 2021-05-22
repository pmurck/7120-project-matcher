package com.pmurck.projectMatcher.model

interface Developer {
    val org: Organization
    val user: User
    val idInOrg: Int
    var availabilityHours: Int
    var seniority: Seniority
    var active: Boolean
    val rankedProjects: List<Project>
    val projectRankings: Map<Project.ID, Int>


    abstract class ID() {
        abstract override fun toString(): String
    }

    fun toID(): ID

    fun save() //todo menos priorities
}

