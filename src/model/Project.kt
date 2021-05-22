package com.pmurck.projectMatcher.model

interface Project {
    val pm: ProjectManager
    val idInPm: Int
    var name: String
    val requirements: List<Requirement>
    var active: Boolean
    val rankedDevelopers: List<Developer>
    val developerRankings: Map<Developer.ID, Int>

    fun getTotalRequiredHours(): Int {
        return this.requirements.stream().mapToInt { it.hours }.sum()
    }

    abstract class ID {
        abstract override fun toString(): String
    }

    fun toID(): ID

    // persiste los cambios
    fun save() //todo menos priorities

    //no persiste -> llamar a save
    fun addRequirement(name: String, seniorities: Set<Seniority>, hours: Int)
}

