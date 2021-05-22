package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.Developer
import com.pmurck.projectMatcher.model.Organization
import com.pmurck.projectMatcher.model.Project
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Route.orgRoutes(dao: DAO) {

    authenticate(UserSession.AUTH_NAME) {
        get<Org> {
            val username = call.sessions.get<UserSession>()!!.name
            val organization = requireNotNull(dao.getOrganization(it.code))
            require(username == organization.admin.name)
            call.respond(FreeMarkerContent("org/index.ftl", mapOf("org" to organization), ""))
        }

        post("/org") {
            val username = call.sessions.get<UserSession>()!!.name
            val param = call.receiveParameters()
            dao.createOrganization(param.getOrFail("organizationName").trim(), username)
            call.respondRedirect(application.locations.href(Index()))
        }

        post("/org/join") {
            // val userSession = call.sessions.get<UserSession>()!!
            // TODO: if admin no lo permito?
            val param = call.receiveParameters()
            val orgCode = param.getOrFail("organizationCode").toUpperCase()
            val joinType = param.getOrFail("joinType")
            if (dao.getOrganization(orgCode) == null){
                setErrors(mapOf("invalid_orgcode" to "No existe organización con ese código"))
                call.respondRedirect(application.locations.href(Index()))
            } else {
                when (joinType) {
                    "pm" -> call.respondRedirect(application.locations.href(Org.PM(Org(orgCode))))
                    "dev" -> call.respondRedirect(application.locations.href(Org.Dev(Org(orgCode))))
                }
            }
        }

        get<Org.Solve> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.org.code))
            require(username == org.admin.name)
            val devs = dao.getActiveDevelopers(it.org.code)
            val projects = dao.getActiveProjects(it.org.code)
            val matcher = Matcher().apply { solve(devs, projects) }
            call.respond(FreeMarkerContent("org/optim.ftl", mapOf("data" to OrgOptimData(org, devs, projects, matcher))))
        }
    }
}

data class OrgOptimData(val org: Organization, val devs: List<Developer>, val projects: List<Project>, val matcher: Matcher)