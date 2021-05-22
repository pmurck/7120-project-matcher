package com.pmurck.projectMatcher.dao.mongo

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.*
import com.pmurck.projectMatcher.model.mongo.*
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.push


class DAOImpl(private val database: MongoDatabase): DAO {
    private val userCol = database.getCollection<User>()
    private val orgCol = database.getCollection<OrgDocument>()

    //TODO: init { createIndex de las claves ....}

    private val defaultReqs by lazy {
        arrayListOf( AbstractRequirementImpl(0, "Senior", setOf(Seniority.SENIOR)),
                AbstractRequirementImpl(1, "Semisenior o superior", setOf(Seniority.SEMISENIOR, Seniority.SENIOR)),
                AbstractRequirementImpl(2, "Junior o superior", setOf(Seniority.JUNIOR, Seniority.SEMISENIOR, Seniority.SENIOR))
        )
    }

    override fun getDefaultRequirements(): List<AbstractRequirement> {
        return defaultReqs
    }

    fun getDevelopers(orgCode: String): List<Developer> {
        return orgCol.aggregate<EmbeddedDev>(
                match(OrgDocument::code eq orgCode),//TODO: exclude?
                OrgDocument::devs.unwind(),
                replaceRoot(OrgDocument::devs),
        ).toList().map {
            DeveloperImpl(this, orgCode, it)
        }.sortedByDescending { it.seniority.ordinal }
    }

    override fun getActiveDevelopers(orgCode: String): List<Developer> {
        return orgCol.aggregate<EmbeddedDev>(
                match(OrgDocument::code eq orgCode),//TODO: exclude?
                OrgDocument::devs.unwind(),
                match(OrgDocument::devs / EmbeddedDev::active eq true),
                replaceRoot(OrgDocument::devs),
        ).toList().map {
            DeveloperImpl(this, orgCode, it)
        }.sortedByDescending { it.seniority.ordinal }
    }

    fun getPMs(orgCode: String): List<ProjectManager> {
        return orgCol.aggregate<EmbeddedPM>(
                match(OrgDocument::code eq orgCode),//TODO: exclude?
                OrgDocument::pms.unwind(),
                replaceRoot(OrgDocument::pms),
        ).toList().map {
            ProjectManagerImpl(this, orgCode, it)
        }
    }

    override fun getProjects(orgCode: String): List<Project> {
        return orgCol.aggregate<EmbeddedProjectWithPMId>(
                match(OrgDocument::code eq orgCode), //TODO: exclude?
                OrgDocument::pms.unwind(),
                replaceRoot(OrgDocument::pms),
                EmbeddedPM::projects.unwind(),
                project(
                    EmbeddedProjectWithPMId::pmIdInOrg from EmbeddedPM::idInOrg,
                        EmbeddedProjectWithPMId::embeddedProject from EmbeddedPM::projects)
        ).toList().map {
            ProjectImpl(this, orgCode, it.pmIdInOrg, it.embeddedProject)
        }
    }

    override fun getActiveProjects(orgCode: String): List<Project> {
        return orgCol.aggregate<EmbeddedProjectWithPMId>(
                match(OrgDocument::code eq orgCode), //TODO: exclude?
                OrgDocument::pms.unwind(),
                replaceRoot(OrgDocument::pms),
                EmbeddedPM::projects.unwind(),
                match(EmbeddedPM::projects / EmbeddedProject::active eq true),
                project(
                    EmbeddedProjectWithPMId::pmIdInOrg from EmbeddedPM::idInOrg,
                        EmbeddedProjectWithPMId::embeddedProject from EmbeddedPM::projects)
        ).toList().map {
            ProjectImpl(this, orgCode, it.pmIdInOrg, it.embeddedProject)
        }
    }

    data class DevProjection(val dev: EmbeddedDev)

    override fun updateProjectRankingsForDeveloper(orgCode: String, username: String, rankedProjectIds: List<String>) {
        requireNotNull(this.getDeveloper(orgCode, username))
        val rankedProjects = rankedProjectIds.map { id -> EmbeddedProjectID.fromString(id)}
        orgCol.updateOne(OrgDocument::code eq orgCode,
                setValue(OrgDocument::devs.filteredPosOp("dev") / EmbeddedDev::priorities, rankedProjects),
                UpdateOptions().arrayFilters(listOf(DevProjection::dev / EmbeddedDev::username eq username)))
    }

    data class PMProjection(val pm: EmbeddedPM)
    data class ProjectProjection(val project: EmbeddedProject)

    override fun updateDevRankingsForProject(orgCode: String, username: String, projectId: Int, rankedDevIds: List<String>) {
        requireNotNull(this.getProject(orgCode, username, projectId))
        val rankedDevs = rankedDevIds.map { id -> EmbeddedDevID.fromString(id) }
        orgCol.updateOne(and(OrgDocument::code eq orgCode, OrgDocument::pms / EmbeddedPM::username eq username),
                setValue(OrgDocument::pms.filteredPosOp("pm") / EmbeddedPM::projects.filteredPosOp("project") / EmbeddedProject::priorities, rankedDevs),
                UpdateOptions().arrayFilters(listOf(PMProjection::pm / EmbeddedPM::username eq username, ProjectProjection::project / EmbeddedProject::idInPM eq projectId)))
    }

    override fun createUser(name: String, passwordHash: String, firstName: String, lastName: String): User {
        require(getUser(name) == null)
        return User(name, passwordHash, firstName, lastName).also { userCol.insertOne(it) }
    }

    override fun getUser(name: String): User? {
        return userCol.findOne(User::name eq name)
    }

    private fun createOrgCode(): String {
        var code: String
        do {
            code = (1..4).map { ('A'..'Z').random() }.joinToString("")
        } while (getOrganization(code) != null)
        return code
    }

    override fun createOrganization(name: String, adminName: String): Organization {
        // Por ahora falla si no existe user
        val admin = this.getUser(adminName)!!
        val code = this.createOrgCode()
        val org = OrgDocument(code, admin.name, name)
        orgCol.insertOne(org)
        return OrganizationImpl(this, org) //TODO: ver counts
    }

    override fun getOrganization(code: String): Organization? {
        return orgCol.aggregate<OrgDocument>(
                match(OrgDocument::code eq code),
                project(exclude(OrgDocument::devs, OrgDocument::pms))
        ).first()?.let{
            OrganizationImpl(this, it)
        }
    }

    override fun getRolesInOrgsFor(username: String): List<Pair<Role, Organization>> {
        val rolesInOrgs = arrayListOf<Pair<Role, Organization>>()

        orgCol.aggregate<OrgDocument>(
                match(OrgDocument::adminUsername eq username),
                project(exclude(OrgDocument::devs, OrgDocument::pms))
        ).forEach { rolesInOrgs.add(Role.ADMIN to OrganizationImpl(this, it)) }

        orgCol.aggregate<OrgDocument>(
                match(OrgDocument::devs / EmbeddedDev::username eq username),
                project(exclude(OrgDocument::devs, OrgDocument::pms))
        ).forEach { rolesInOrgs.add(Role.DEV to OrganizationImpl(this, it)) }

        orgCol.aggregate<OrgDocument>(
                match(OrgDocument::pms / EmbeddedPM::username eq username),
                project(exclude(OrgDocument::devs, OrgDocument::pms))
        ).forEach { rolesInOrgs.add(Role.PM to OrganizationImpl(this, it)) }

        return rolesInOrgs
    }

    private fun getDeveloper(orgCode: String, secondMatchCondition: Bson): Developer? {
        return orgCol.aggregate<EmbeddedDev>(
                match(OrgDocument::code eq orgCode),
                OrgDocument::devs.unwind(),
                match(secondMatchCondition),
                replaceRoot(OrgDocument::devs)
        ).first()?.let {
            DeveloperImpl(this, orgCode, it)
        }
    }

    override fun getDeveloper(orgCode: String, devId: Int): Developer? {
        return this.getDeveloper(orgCode, OrgDocument::devs / EmbeddedDev::idInOrg eq devId)
    }

    override fun getDeveloper(orgCode: String, username: String): Developer? {
        return this.getDeveloper(orgCode, OrgDocument::devs / EmbeddedDev::username eq username)
    }

    override fun createDeveloper(orgCode: String, username: String, availabilityHours: Int, seniority: Seniority): Developer {
        require(this.getDeveloper(orgCode, username) == null)

        val organization = requireNotNull(this.getOrganization(orgCode))
        val user = requireNotNull(this.getUser(username))
        // TODO: ojo race condition sobre devCount
        val embDev = EmbeddedDev(user.name, organization.devCount, availabilityHours, seniority.toString())
        orgCol.bulkWrite(
                updateOne(OrgDocument::code eq organization.code, push(OrgDocument::devs, embDev)),
                updateOne(OrgDocument::code eq organization.code, inc(OrgDocument::devCount, 1))
        )
        return DeveloperImpl(this, orgCode, embDev)
    }

    fun updateDeveloper(developer: DeveloperImpl) {
        orgCol.updateOne(OrgDocument::code eq developer.orgCode, set(
                    OrgDocument::devs.filteredPosOp("dev") / EmbeddedDev::availabilityHours setTo developer.availabilityHours,
                    OrgDocument::devs.filteredPosOp("dev") / EmbeddedDev::seniority setTo developer.seniority.name,
                    OrgDocument::devs.filteredPosOp("dev") / EmbeddedDev::active setTo developer.active),
                UpdateOptions().arrayFilters(listOf(DevProjection::dev / EmbeddedDev::idInOrg eq developer.idInOrg))
            )

    }

    override fun createPM(orgCode: String, username: String): ProjectManager {
        require(this.getPM(orgCode, username) == null)

        val organization = requireNotNull(this.getOrganization(orgCode))
        val user = requireNotNull(this.getUser(username))
        // TODO: ojo race condition sobre pmCount
        val embPM = EmbeddedPM(user.name, organization.pmCount)
        orgCol.bulkWrite(
                updateOne(OrgDocument::code eq orgCode, push(OrgDocument::pms, embPM)),
                updateOne(OrgDocument::code eq organization.code, inc(OrgDocument::pmCount, 1))
        )

        return ProjectManagerImpl(this, orgCode, embPM)
    }

    private fun getPM(orgCode: String, pmMatchCondition: Bson): ProjectManager? {
        return orgCol.aggregate<EmbeddedPM>(
                match(OrgDocument::code eq orgCode),
                OrgDocument::pms.unwind(),
                match(pmMatchCondition),
                replaceRoot(OrgDocument::pms)
        ).first()?.let {
            ProjectManagerImpl(this, orgCode, it)
        }
    }

    override fun getPM(orgCode: String, username: String): ProjectManager? {
        return getPM(orgCode, OrgDocument::pms / EmbeddedPM::username eq username)
    }

    fun getPM(orgCode: String, pmId: Int): ProjectManager? {
        return getPM(orgCode, OrgDocument::pms / EmbeddedPM::idInOrg eq pmId)
    }

    // si no hay PM tambien lo creamos
    // POR AHORA: indice en array == id de proyecto
    override fun createProject(orgCode: String, username: String, projectName: String, prePersistModification: (Project) -> Unit): Project {
        val pm = requireNotNull(this.getPM(orgCode, username))

        val embProj = EmbeddedProject(pm.projects.size, projectName)
        val project = ProjectImpl(this, orgCode, pm.idInOrg, embProj)
        prePersistModification(project)

        orgCol.updateOne(and(OrgDocument::code eq orgCode, OrgDocument::pms / EmbeddedPM::username eq username),
                push(OrgDocument::pms.posOp / EmbeddedPM::projects, embProj))
        return project
    }

    data class EmbeddedProjectWithPMId(val pmIdInOrg: Int, val embeddedProject: EmbeddedProject)

    private fun getProject(orgCode: String, pmMatchCondition: Bson, projectId: Int): Project? {
        return orgCol.aggregate<EmbeddedProjectWithPMId>(
                match(OrgDocument::code eq orgCode),
                OrgDocument::pms.unwind(),
                match(pmMatchCondition),
                replaceRoot(OrgDocument::pms),
                EmbeddedPM::projects.unwind(),
                match(EmbeddedPM::projects / EmbeddedProject::idInPM eq projectId),
                project(
                    EmbeddedProjectWithPMId::pmIdInOrg from EmbeddedPM::idInOrg,
                        EmbeddedProjectWithPMId::embeddedProject from EmbeddedPM::projects)
        ).first()?.let {
            ProjectImpl(this, orgCode, it.pmIdInOrg, it.embeddedProject)
        }
    }

    override fun getProject(orgCode: String, username: String, projectId: Int): Project? {
        return this.getProject(orgCode, OrgDocument::pms / EmbeddedPM::username eq username, projectId)
    }

    override fun getProject(orgCode: String, pmId: Int, projectId: Int): Project? {
        return this.getProject(orgCode, OrgDocument::pms / EmbeddedPM::idInOrg eq pmId, projectId)
    }

    fun updateProject(project: ProjectImpl) {
        orgCol.updateOne(
                OrgDocument::code eq project.orgCode,
                setValue(OrgDocument::pms.filteredPosOp("pm") / EmbeddedPM::projects.filteredPosOp("project"),
                    project.embeddedProject),
                UpdateOptions().arrayFilters(
                    listOf(PMProjection::pm / EmbeddedPM::idInOrg eq project.pmIdInOrg,
                        ProjectProjection::project / EmbeddedProject::idInPM eq project.idInPm)))
    }
}