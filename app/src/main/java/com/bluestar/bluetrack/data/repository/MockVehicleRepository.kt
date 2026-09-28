package com.bluestar.bluetrack.data.repository

import com.bluestar.bluetrack.domain.model.Vehicle
import com.bluestar.bluetrack.domain.repository.VehicleRepository
import kotlinx.coroutines.delay

class MockVehicleRepository : VehicleRepository {
    override suspend fun getVehicles(): List<Vehicle> {
        delay(300)
        return listOf(
            Vehicle(
                id = "veh-001",
                name = "T-01 Trailer Caja Seca",
                plate = "PBA-123-A",
                status = "En movimiento",
                location = "Autopista México–Querétaro Km 40",
                speedKmh = 85,
                batteryPercent = 92,
                lastUpdate = "Actualizado hace 2 min"
            ),
            Vehicle(
                id = "veh-002",
                name = "T-02 Trailer Refrigerado",
                plate = "PBA-124-A",
                status = "Detenido",
                location = "Centro de Distribución CDMX",
                speedKmh = 0,
                batteryPercent = 78,
                lastUpdate = "Actualizado hace 4 min"
            ),
            Vehicle(
                id = "veh-003",
                name = "T-03 Unidad de reparto",
                plate = "PBA-125-A",
                status = "En movimiento",
                location = "Periférico Norte, CDMX",
                speedKmh = 61,
                batteryPercent = 86,
                lastUpdate = "Actualizado hace 1 min"
            )
        )
    }
}