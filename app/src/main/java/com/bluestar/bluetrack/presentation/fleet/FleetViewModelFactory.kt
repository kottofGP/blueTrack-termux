package com.bluestar.bluetrack.presentation.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bluestar.bluetrack.domain.repository.VehicleRepository

class FleetViewModelFactory(
    private val vehicleRepository: VehicleRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FleetViewModel::class.java)) {
            return FleetViewModel(vehicleRepository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}