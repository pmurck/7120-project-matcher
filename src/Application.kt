package com.pmurck.projectMatcher

import com.pmurck.projectMatcher.dao.DAO
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import kotlin.collections.mapOf
import kotlin.collections.set

data class UserSession(val name: String): Principal {
    companion object {
        val AUTH_NAME = "sessionAuth"
    }
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@KtorExperimentalLocationsAPI
fun Application.module() {
    val dao: DAO by inject()

    install(Koin){
        modules(koinModule())
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(Compression) {
        gzip()
    }

    install(Locations) {
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(StatusPages) {
        // TODO: mejor manejo de errores
        exception<IllegalArgumentException> {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    install(Authentication) {
        form(Constants.FORM_AUTH) {
            this.userParamName = "username"
            this.passwordParamName = "password"
            challenge {
                setErrors(mapOf("invalid_login" to "Usuario o contraseña inválidos"))
                call.respondRedirect("/login")
            }
            validate { userPasswordCredential: UserPasswordCredential ->
                val user = dao.getUser(userPasswordCredential.name)
                if (user?.passwordHash == userPasswordCredential.password) {
                    UserSession(user.name) //TODO: hashear username
                } else {
                    null
                }
            }
        }
        session<UserSession>(UserSession.AUTH_NAME) {
            challenge {
                // What to do if the user isn't authenticated
                call.respondRedirect(application.locations.href(Login()))
            }
            validate { session: UserSession ->
                // If you need to do additional validation on session data, you can do so here.
                session.takeIf { dao.getUser(session.name) != null }
            }
        }
    }

    routing {
        // Static feature.
        static("/static") {
            resources("static")
        }

        get("/loadexamples") {
            dao.initExampleOrg()
            dao.initBigExample()
            call.respond("Done!")
        }

        userRoutes(get())
        orgRoutes(get())
        devRoutes(get())
        pmRoutes(get())
    }
}
