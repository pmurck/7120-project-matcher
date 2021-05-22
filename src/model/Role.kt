package com.pmurck.projectMatcher.model

enum class Role(val desc: String) {
    ADMIN("Admin. General") {
        override fun hrefToIndex(orgCode: String): String {
            return "/org/${orgCode}"
        }
    },
    DEV("Desarrollador") {
        override fun hrefToIndex(orgCode: String): String {
            return "/org/${orgCode}/dev"
        }
    },
    PM("Admin. de Proyectos") {
        override fun hrefToIndex(orgCode: String): String {
            return "/org/${orgCode}/pm"
        }
    };

    abstract fun hrefToIndex(orgCode: String): String
}