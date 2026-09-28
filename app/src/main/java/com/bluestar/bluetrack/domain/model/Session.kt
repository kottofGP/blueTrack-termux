package com.bluestar.bluetrack.domain.model

data class Session(
    val user: User,
    val isAuthenticated: Boolean
)
