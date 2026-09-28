package com.bluestar.bluetrack.domain.model

data class Vehicle(
    val id: String,
    val name: String,
    val plate: String = "",
    val status: String = "offline",
    val location: String = "Sin ubicación",
    val speedKmh: Int = 0,
    val batteryPercent: Int = 100,
    val lastUpdate: String = "Sin actualización"
)
