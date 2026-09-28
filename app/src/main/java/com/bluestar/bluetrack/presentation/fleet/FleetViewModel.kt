package com.bluestar.bluetrack.presentation.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestar.bluetrack.domain.model.Vehicle
import com.bluestar.bluetrack.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FleetState(
    val isLoading: Boolean = false,
    val vehicles: List<Vehicle> = emptyList(),
    val error: String? = null
)

class FleetViewModel(
    private val vehicleRepository: VehicleRepository
) : ViewModel() {
    private val _state = MutableStateFlow(FleetState())
    val state: StateFlow<FleetState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            runCatching { vehicleRepository.getVehicles() }
                .onSuccess { vehicles ->
                    _state.value = FleetState(vehicles = vehicles)
                }
                .onFailure {
                    _state.value = FleetState(error = "No pudimos cargar tu flota")
                }
        }
    }
}