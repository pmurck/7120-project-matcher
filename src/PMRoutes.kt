package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.AbstractRequirement
import com.pmurck.projectMatcher.model.Organization
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
fun Route.pmRoutes(dao: DAO) {

    authenticate(UserSession.AUTH_NAME) {
        get<Org.PM> {
            val username = call.sessions.get<UserSession>()!!.name
            requireNotNull(dao.getOrganization(it.org.code))
            val pm = dao.getPM(it.org.code, username)
            if (pm == null) {
                call.respondRedirect(application.locations.href(Org.PM.ProjectNew(it)))
            } else {
                call.respond(FreeMarkerContent("pm/index.ftl", mapOf("pm" to pm)))
            }
        }

        get<Org.PM.ProjectNew> {
            val organization = requireNotNull(dao.getOrganization(it.pm.org.code))
            // TODO: mostrar el listadito de sets de seniority que compone el req (letra chica o algo asi)
            call.respond(FreeMarkerContent("pm/project/new.ftl", mapOf("data" to ProjectNewData(organization, dao.getDefaultRequirements()))))
        }

        post<Org.PM.ProjectNew> {
            val username = call.sessions.get<UserSession>()!!.name
            val params = call.receiveParameters()
            val projectName = params.getOrFail("projectName")

            if (dao.getPM(it.pm.org.code, username) == null) {
                dao.createPM(it.pm.org.code, username)
            }

            val requirements = dao.getDefaultRequirements()
            dao.createProject(it.pm.org.code, username, projectName) { project ->
                for (req in requirements) {
                    val hours = params[req.prefixedId()]!!.toInt()
                    project.addRequirement(req.name, req.seniorities, hours)
                }
            }

            call.respondRedirect(application.locations.href(it.pm))
        }

        get<Org.PM.Proj> {
            val username = call.sessions.get<UserSession>()!!.name
            val project = requireNotNull(dao.getProject(it.pm.org.code, username, it.id))
            call.respond(FreeMarkerContent("pm/project/index.ftl", mapOf("project" to project)))
        }

        get<Org.PM.Proj.Edit> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.proj.pm.org.code))
            val project = requireNotNull(if (it.pmId != null) {
                require(username == org.admin.name)
                dao.getProject(it.proj.pm.org.code, it.pmId, it.proj.id)
            } else {
                dao.getProject(it.proj.pm.org.code, username, it.proj.id)
            })
            // TODO: mostrar el listadito de sets que compone el req (letra chica o algo asi)
            call.respond(FreeMarkerContent("pm/project/edit.ftl", mapOf("project" to project)))
        }

        post<Org.PM.Proj.Edit> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.proj.pm.org.code))
            val (project, redirectLocation) = if (it.pmId != null) {
                require(username == org.admin.name)
                requireNotNull(dao.getProject(it.proj.pm.org.code, it.pmId, it.proj.id)) to it.proj.pm.org
            } else {
                requireNotNull(dao.getProject(it.proj.pm.org.code, username, it.proj.id)) to it.proj
            }

            val params = call.receiveParameters()
            project.name = params.getOrFail("projectName")
            for (req in project.requirements) {
                req.hours = params[req.prefixedId()]!!.toInt()
            }
            dao.updateProject(project)
            call.respondRedirect(application.locations.href(redirectLocation))
        }

        get<Org.PM.Proj.Priorities> {
            val username = call.sessions.get<UserSession>()!!.name
            val project = requireNotNull(dao.getProject(it.proj.pm.org.code, username, it.proj.id))
            call.respond(FreeMarkerContent("pm/project/priorities.ftl", mapOf("prioritizedDevs" to project.rankedDevelopers)))
        }

        post<Org.PM.Proj.Priorities> {
            val username = call.sessions.get<UserSession>()!!.name
            val param = call.receiveParameters()
            param.getAll("devs")?.let { devIds -> // puede no haber
                dao.updateDevRankingsForProject(it.proj.pm.org.code, username, it.proj.id, devIds)
            }
            call.respondRedirect(application.locations.href(it.proj))
        }

        get<Org.PM.Proj.Assignments> { assignments ->
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(assignments.proj.pm.org.code))
            val project = requireNotNull(dao.getProject(org.code, username, assignments.proj.id))
            val devs = dao.getActiveDevelopers(assignments.proj.pm.org.code)
            val projects = dao.getActiveProjects(assignments.proj.pm.org.code)
            val matcher = Matcher().apply { solve(devs, projects) }
            call.respond(FreeMarkerContent("org/optim.ftl",
                    mapOf("data" to OrgOptimData(org, devs,
                            projects.filter { it.pm.user.name == username && it.idInPm == project.idInPm }, matcher))))
        }

        get<Org.PM.Proj.SwitchState> {
            val username = call.sessions.get<UserSession>()!!.name
            val org = requireNotNull(dao.getOrganization(it.proj.pm.org.code))
            require(username == org.admin.name)
            val project = requireNotNull(dao.getProject(it.proj.pm.org.code, it.pmId, it.proj.id))
            project.apply { active = !active }.save()
            call.respondRedirect(application.locations.href(it.proj.pm.org))
        }
    }
}

data class ProjectNewData(val organization: Organization, val requirements: List<AbstractRequirement>)