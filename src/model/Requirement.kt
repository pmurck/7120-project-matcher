package com.pmurck.projectMatcher.model

// TODO: cambiar nombre
interface AbstractRequirement {
    val id: Int
    val name: String
    val seniorities: Set<Seniority>

    fun prefixedId(): String {
        return "req_${id}"
    }
}

interface Requirement: AbstractRequirement {
    val project: Project
    val idInProject: Int
    var hours: Int

    override val id: Int get() = idInProject

    abstract class ID() {
        abstract override fun toString(): String
    }

    fun toID(): ID
}




