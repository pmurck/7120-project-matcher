package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.Developer
import com.pmurck.projectMatcher.model.Seniority
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

@KtorExperimentalLocationsAPI
fun Route.devRoutes(dao: DAO) {

    authenticate(UserSession.AUTH_NAME) {
        get<Org.Dev> {
            val username = call.sessions.get<UserSession>()!!.name
            requireNotNull(dao.getOrganization(it.org.code))
            val dev = dao.getDeveloper(it.org.code, username)
            if (dev == null) {
                call.respondRedirect(application.locations.href(Org.Dev.Edit(it)))
            } else {
                call.respond(FreeMarkerContent("dev/index.ftl", mapOf("dev" to dev)))
            }
        }

        get<Org.Dev.Edit> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.dev.org.code))
            val dev = if (it.devId != null) {
                require(username == org.admin.name) // sea admin
                requireNotNull(dao.getDeveloper(it.dev.org.code, it.devId))
            } else {
                dao.getDeveloper(it.dev.org.code, username) // puede ser null
            }
            call.respond(FreeMarkerContent("dev/edit.ftl", mapOf("data" to DevEditData(dev, Seniority.values()))))
        }

        post<Org.Dev.Edit> {
            val username = call.sessions.get<UserSession>()!!.name
            val param = call.receiveParameters() //TODO: check params
            val (dev, locationRedirect) = if (it.devId != null) {
                // Edit del admin, check que sea
                require(username == requireNotNull(dao.getOrganization(it.dev.org.code)).admin.name)
                requireNotNull(dao.getDeveloper(it.dev.org.code, it.devId)) to it.dev.org// solo edit, no create
            } else {
                dao.getDeveloper(it.dev.org.code, username) to it.dev
            }

            // porque esto es para el edit y para el nuevo
            if (dev == null) {
                dao.createDeveloper(it.dev.org.code, username, param["availabilityHours"]!!.toInt(),
                        Seniority.valueOf(param["seniority"]!!))
            } else {
                dev.apply {
                    this.availabilityHours = param["availabilityHours"]!!.toInt()
                    this.seniority = Seniority.valueOf(param["seniority"]!!)
                }.save()
            }

            call.respondRedirect(application.locations.href(locationRedirect))
        }

        get<Org.Dev.Priorities> {
            val username = call.sessions.get<UserSession>()!!.name
            val dev = requireNotNull(dao.getDeveloper(it.dev.org.code, username))
            call.respond(FreeMarkerContent("dev/priorities.ftl", mapOf("prioritizedProjects" to dev.rankedProjects)))
        }

        post<Org.Dev.Priorities> {
            val username = call.sessions.get<UserSession>()!!.name
            val param = call.receiveParameters()
            param.getAll("projects")?.let { projectIds -> // puede no haber
                dao.updateProjectRankingsForDeveloper(it.dev.org.code, username, projectIds)
            }
            call.respondRedirect(application.locations.href(it.dev))
        }

        get<Org.Dev.SwitchState> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.dev.org.code))
            require(username == org.admin.name)
            val dev = requireNotNull(dao.getDeveloper(it.dev.org.code, it.devId))
            dev.apply { active = !active }.save()
            call.respondRedirect(application.locations.href(it.dev.org))
        }

        get<Org.Dev.Assignments> { assignments ->
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(assignments.dev.org.code))
            requireNotNull(dao.getDeveloper(org.code, username))
            val devs = dao.getActiveDevelopers(assignments.dev.org.code)
            val projects = dao.getActiveProjects(assignments.dev.org.code)
            val matcher = Matcher().apply { solve(devs, projects) }
            call.respond(FreeMarkerContent("org/optim.ftl",
                mapOf(
                    "data" to OrgOptimData(
                        org,
                        devs.filter { it.user.name == username },
                        projects,
                        matcher))))
        }
    }
}

data class DevEditData(val dev: Developer?, val seniorities: Array<Seniority>)