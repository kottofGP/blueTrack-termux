package com.bluestar.bluetrack.domain.repository

import com.bluestar.bluetrack.domain.model.Vehicle

interface VehicleRepository {

    suspend fun getVehicles(): List<Vehicle>
}
