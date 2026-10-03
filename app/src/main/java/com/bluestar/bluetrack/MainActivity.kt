package com.bluestar.bluetrack

import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluestar.bluetrack.data.repository.MockAuthRepository
import com.bluestar.bluetrack.data.repository.MockVehicleRepository
import com.bluestar.bluetrack.domain.model.Vehicle
import com.bluestar.bluetrack.presentation.dashboard.DashboardScreen
import com.bluestar.bluetrack.presentation.fleet.FleetViewModel
import com.bluestar.bluetrack.presentation.fleet.FleetViewModelFactory
import com.bluestar.bluetrack.presentation.login.LoginScreen
import com.bluestar.bluetrack.presentation.login.LoginViewModel
import com.bluestar.bluetrack.presentation.login.LoginViewModelFactory
import com.bluestar.bluetrack.presentation.vehicle.VehicleDetailScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val background = Color.rgb(15, 17, 21)

    private lateinit var root: FrameLayout
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fleetViewModel: FleetViewModel

    private var loginScreen: LoginScreen? = null
    private var dashboardScreen: DashboardScreen? = null
    private var vehicleDetailScreen: VehicleDetailScreen? = null

    private var dashboardVisible = false
    private var currentDisplayName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = background
        window.navigationBarColor = background

        root = FrameLayout(this).apply {
            setBackgroundColor(this@MainActivity.background)
        }

        setContentView(root)

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(MockAuthRepository())
        )[LoginViewModel::class.java]

        fleetViewModel = ViewModelProvider(
            this,
            FleetViewModelFactory(MockVehicleRepository())
        )[FleetViewModel::class.java]

        observeState()
        showLogin()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    loginViewModel.state.collect { state ->

                        loginScreen?.renderState(state)

                        if (
                            state.session?.isAuthenticated == true &&
                            !dashboardVisible
                        ) {
                            dashboardVisible = true

                            fleetViewModel.load()

                            showDashboard(
                                state.session.user.displayName
                                    .orEmpty()
                                    .ifBlank {
                                        state.session.user.username
                                    }
                            )
                        }
                    }
                }

                launch {
                    fleetViewModel.state.collect { state ->
                        if (dashboardVisible) {
                            dashboardScreen?.render(state)
                        }
                    }
                }
            }
        }
    }

    private fun showLogin() {
        dashboardVisible = false
        dashboardScreen = null

        val screen = LoginScreen(
            context = this,
            root = root,
            onLogin = { username, password ->
                loginViewModel.login(username, password)
            }
        )

        loginScreen = screen

        screen.show()
    }

    private fun showDashboard(displayName: String) {
        loginScreen = null
        vehicleDetailScreen = null
        dashboardVisible = true
        currentDisplayName = displayName

        val screen = DashboardScreen(
            root = root,
            displayName = displayName,
            onLogout = {
                loginViewModel.logout()
                showLogin()
            },
            onRefresh = {
                fleetViewModel.load()
            },
            onVehicleSelected = { vehicle ->
                showVehicleDetail(vehicle)
            }
        )

        dashboardScreen = screen

        screen.show()
        screen.render(fleetViewModel.state.value)
    }


    private fun showVehicleDetail(vehicle: Vehicle) {
        dashboardVisible = false
        dashboardScreen = null

        val screen = VehicleDetailScreen(
            root = root,
            vehicle = vehicle,
            onBack = {
                showDashboard(currentDisplayName)
            }
        )

        vehicleDetailScreen = screen
        screen.show()
    }
}
