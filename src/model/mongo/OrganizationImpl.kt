package com.pmurck.projectMatcher.model.mongo

import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import com.pmurck.projectMatcher.model.*

data class OrganizationImpl(
    val dao: DAOImpl,
    val orgDoc: OrgDocument) : Organization {

    override val admin: User by lazy {
        this.dao.getUser(orgDoc.adminUsername)!!
    }

    override val code by orgDoc::code
    override val name by orgDoc::name
    override val devCount by orgDoc::devCount
    override val pmCount by orgDoc::pmCount


    override val devs: List<Developer> by lazy {
        this.dao.getDevelopers(this.code)
    }

    override val pms: List<ProjectManager> by lazy {
        this.dao.getPMs(this.code)
    }

}