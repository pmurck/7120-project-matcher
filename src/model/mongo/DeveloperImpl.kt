package com.pmurck.projectMatcher.model.mongo

import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import com.pmurck.projectMatcher.model.*

data class DeveloperImpl(
    val dao: DAOImpl,
    val orgCode: String,
    val embeddedDev: EmbeddedDev
): Developer {

    override val org: Organization by lazy {
        dao.getOrganization(this.orgCode)!!
    }

    override val user: User by lazy {
        dao.getUser(embeddedDev.username)!!
    }

    override val idInOrg: Int by embeddedDev::idInOrg
    override var availabilityHours: Int by embeddedDev::availabilityHours
    override var seniority: Seniority
        get() = Seniority.valueOf(embeddedDev.seniority)
        set(value) {
            embeddedDev.seniority = value.name
        }

    override var active: Boolean by embeddedDev::active

    data class ID(val devID: EmbeddedDevID) : Developer.ID() {

        override fun toString(): String {
            return devID.toString()
        }
    }

    private val _id: Developer.ID by lazy {
        ID(EmbeddedDevID(this.orgCode, this.idInOrg))
    }

    override fun toID(): Developer.ID {
        return _id
    }

    override fun save() {
        dao.updateDeveloper(this)
    }

    // fuerza completitud
    override val rankedProjects: List<Project> by lazy {
        val projects = dao.getProjects(this.orgCode).associateBy { it.toID() }.toMutableMap()
        embeddedDev.priorities.map { projects.remove(ProjectImpl.ID(it))!! }.toMutableList().apply{
            this.addAll(projects.values)
        }
    }

    // proj -> posicion (1..map.size inclusive)
    override val projectRankings: Map<Project.ID, Int>
        get() {
            return this.rankedProjects.mapIndexed { index, developer -> developer.toID() to index+1 }.toMap()
        }
}