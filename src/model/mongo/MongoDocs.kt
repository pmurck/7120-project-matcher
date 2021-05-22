package com.pmurck.projectMatcher.model

data class EmbeddedDev(
        val username: String,
        val idInOrg: Int,
        var availabilityHours: Int,
        var seniority: String,
        var active: Boolean = true,
        var priorities: MutableList<EmbeddedProjectID> = arrayListOf()
)

data class EmbeddedDevID(
        val orgCode: String,
        val idInOrg: Int
) {

    companion object {
        private val DELIMITER = "_&=&_"
        fun fromString(id: String): EmbeddedDevID {
            with(id.split(DELIMITER)) {
                // this[0] == "dev"
                return EmbeddedDevID(this[1], this[2].toInt())
            }
        }
    }

    override fun toString(): String {
        return "dev${DELIMITER}${orgCode}${DELIMITER}${idInOrg}"
    }

}

data class EmbeddedPM(
        val username: String,
        val idInOrg: Int,
        val projects: ArrayList<EmbeddedProject> = arrayListOf()
)

data class EmbeddedProjectID(
        val orgCode: String,
        val pmIdInOrg: Int,
        val projectIdInPm: Int
) {
    companion object {
        private val DELIMITER = "_&=&_"
        fun fromString(id: String): EmbeddedProjectID {
            with(id.split(DELIMITER)){
                // this[0] == "proj"
                return EmbeddedProjectID(this[1], this[2].toInt(), this[3].toInt())
            }
        }
    }

    override fun toString(): String {
        return "proj${DELIMITER}${orgCode}${DELIMITER}${pmIdInOrg}${DELIMITER}${projectIdInPm}"
    }
}

data class EmbeddedProject(
        val idInPM: Int,
        var name: String,
        var active: Boolean = true,
        var requirements: MutableList<EmbeddedRequirement> = arrayListOf(),
        var priorities: MutableList<EmbeddedDevID> = arrayListOf()
)

data class EmbeddedRequirement(
        val idInProject: Int,
        val name: String,
        val seniorities: Set<String>,
        var hours: Int
)

data class OrgDocument(
    val code: String,
    val adminUsername: String,
    val name: String,
    val devs: ArrayList<EmbeddedDev> = arrayListOf(),
    val devCount: Int = 0,
    val pms: ArrayList<EmbeddedPM> = arrayListOf(),
    val pmCount: Int = 0
)