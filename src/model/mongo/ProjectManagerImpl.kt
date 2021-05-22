package com.pmurck.projectMatcher.model.mongo

import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import com.pmurck.projectMatcher.model.*

data class ProjectManagerImpl(
    val dao: DAOImpl,
    val orgCode: String,
    val embeddedPM: EmbeddedPM) : ProjectManager {

    override val idInOrg: Int by embeddedPM::idInOrg

    override val org: Organization by lazy {
        dao.getOrganization(orgCode)!!
    }

    override val user: User by lazy {
        dao.getUser(embeddedPM.username)!!
    }

    override val projects: List<Project> by lazy {
        embeddedPM.projects.map { ProjectImpl(dao, orgCode, idInOrg, it) }
    }
}