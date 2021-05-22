package com.pmurck.projectMatcher.model

// TODO: VER username NO puede tener guion bajo "_"
data class User( //email
    val name: String,
    val passwordHash: String,
    val firstName: String,
    val lastName: String
    )

