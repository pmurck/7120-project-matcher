package com.pmurck.projectMatcher.model.mongo

import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import com.pmurck.projectMatcher.model.*

// Esto podria estar en la base
data class AbstractRequirementImpl(
    override val id: Int,
    override val name: String,
    override val seniorities: Set<Seniority>): AbstractRequirement

// name = description   id -> ver
data class RequirementImpl(
    val dao: DAOImpl,
    val orgCode: String,
    val pmIdInOrg: Int,
    val projectIdInPm: Int,
    val embeddedRequirement: EmbeddedRequirement
): Requirement {

    override val project: Project by lazy {
        dao.getProject(orgCode, pmIdInOrg, projectIdInPm)!!
    }
    override val idInProject: Int by embeddedRequirement::idInProject
    override val name: String by embeddedRequirement::name
    override val seniorities: Set<Seniority> by lazy {
        embeddedRequirement.seniorities.map { Seniority.valueOf(it)}.toSet()
    }
    override var hours: Int by embeddedRequirement::hours

    data class ID(val orgCode: String, val pmId: Int, val projectId: Int, val reqId: Int): Requirement.ID() {
        override fun toString(): String {
            return "projReq_${orgCode}_${pmId}_${projectId}_${reqId}"
        }
    }

    private val _id: ID by lazy {
        ID(orgCode, pmIdInOrg, projectIdInPm, this.idInProject)
    }

    override fun toID(): ID {
        return _id
    }

}