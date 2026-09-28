package com.bluestar.bluetrack.domain.model

data class User(
    val id: String,
    val username: String,
    val displayName: String? = null
)
