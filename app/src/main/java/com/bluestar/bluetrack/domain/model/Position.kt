package com.bluestar.bluetrack.domain.model

data class Position(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Double? = null,
    val heading: Double? = null
)
