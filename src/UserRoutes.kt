package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.model.Organization
import com.pmurck.projectMatcher.model.Role
import com.pmurck.projectMatcher.model.User
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
fun Route.userRoutes(dao: DAO) {

    get<Register> {
        call.respond(FreeMarkerContent("register.ftl", mapOf("errors" to getErrors())))
    }

    post<Register> {
        val params = call.receiveParameters()
        val username = params.getOrFail("username")
        val pass = params.getOrFail("password")
        val firstName = params.getOrFail("firstName")
        val lastName = params["lastName"] ?: ""

        if (dao.getUser(username) != null){
            setErrors(mapOf("existing_username" to "Nombre de usuario en uso. Elegir otro."))
            call.respondRedirect(application.locations.href(it))
        } else {
            // TODO: hashear pass
            dao.createUser(username, pass, firstName, lastName)
            call.respondRedirect(application.locations.href(Login()))
        }
    }

    get<Login>{
        val errors = getErrors()
        call.respond(FreeMarkerContent("login.ftl", mapOf("errors" to errors)))
    }

    authenticate(Constants.FORM_AUTH) {
        post<Login> {
            // Pasamos por el auth del form, creamos la sesion
            val principal = call.principal<UserSession>()!!
            call.sessions.set(principal)
            call.respondRedirect(application.locations.href(Index()))
        }
    }

    authenticate(UserSession.AUTH_NAME) {
        get<Index> {
            val username = call.sessions.get<UserSession>()!!.name
            val errors = getErrors()
            call.respond(FreeMarkerContent("index.ftl",
                mapOf("data" to IndexData(dao.getRolesInOrgsFor(username),
                    checkNotNull(dao.getUser(username))), "errors" to errors), ""))
        }

        post<Logout> {
            call.sessions.clear<UserSession>()
            call.respondRedirect(application.locations.href(Login()))
        }
    }
}

data class IndexData(val roles: List<Pair<Role, Organization>>, val user: User)