package com.pmurck.projectMatcher

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.util.pipeline.*
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

object Constants {
    val FORM_AUTH = "formAuth"
}


// TODO: ver el tema de errores
fun PipelineContext<*, ApplicationCall>.getErrors(): Map<String, String> {
    val jsonString = call.request.cookies["errors"] ?: "{}"
    call.response.cookies.appendExpired("errors")
    return JSONParser().parse(jsonString) as Map<String, String>
}

fun PipelineContext<*, ApplicationCall>.setErrors(errors: Map<String, String>): Unit {
    call.response.cookies.append(Cookie("errors", JSONObject.toJSONString(errors), path = "/"))
}