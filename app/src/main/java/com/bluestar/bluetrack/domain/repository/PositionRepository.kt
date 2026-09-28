package com.bluestar.bluetrack.domain.repository

import com.bluestar.bluetrack.domain.model.Position

interface PositionRepository {

    suspend fun getCurrentPosition(vehicleId: String): Position?
}
