package com.pmurck.projectMatcher

import io.ktor.locations.*

@KtorExperimentalLocationsAPI
@Location("/")
class Index

@KtorExperimentalLocationsAPI
@Location("/login")
class Login

@KtorExperimentalLocationsAPI
@Location("/logout")
class Logout

@KtorExperimentalLocationsAPI
@Location("/register")
class Register

@KtorExperimentalLocationsAPI
@Location("/org/{code}")
data class Org(val code: String) { //TODO: ver "/org" y "org/join"
    @Location("/dev")
    data class Dev(val org: Org) {
        @Location("/edit")
        data class Edit(val dev: Dev, val devId: Int? = null)
        @Location("/priorities")
        data class Priorities(val dev: Dev)
        @Location("/assignments")
        data class Assignments(val dev: Dev)
        @Location("/switchState")
        data class SwitchState(val dev: Dev, val devId: Int)
    }
    @Location("/pm")
    data class PM(val org: Org) {
        @Location("/project/new")
        data class ProjectNew(val pm: PM)
        @Location("/project/{id}")
        data class Proj(val pm: PM, val id: Int) {
            @Location("/edit")
            data class Edit(val proj: Proj, val pmId: Int? = null)
            @Location("/priorities")
            data class Priorities(val proj: Proj)
            @Location("/assignments")
            data class Assignments(val proj: Proj)
            @Location("/switchState")
            data class SwitchState(val proj: Proj, val pmId: Int)
        }
    }
    @Location("/solve")
    data class Solve(val org: Org)
}