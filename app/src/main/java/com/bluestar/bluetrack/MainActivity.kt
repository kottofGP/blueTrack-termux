package com.bluestar.bluetrack

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bluestar.bluetrack.data.repository.MockAuthRepository
import com.bluestar.bluetrack.data.repository.MockVehicleRepository
import com.bluestar.bluetrack.domain.model.Vehicle
import com.bluestar.bluetrack.presentation.fleet.FleetState
import com.bluestar.bluetrack.presentation.fleet.FleetViewModel
import com.bluestar.bluetrack.presentation.fleet.FleetViewModelFactory
import com.bluestar.bluetrack.presentation.login.LoginState
import com.bluestar.bluetrack.presentation.login.LoginViewModel
import com.bluestar.bluetrack.presentation.login.LoginViewModelFactory
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val blue = Color.rgb(0, 124, 255)
    private val background = Color.rgb(15, 17, 21)
    private val card = Color.rgb(24, 28, 32)
    private val border = Color.rgb(34, 40, 47)
    private val textPrimary = Color.rgb(239, 241, 243)
    private val textMuted = Color.rgb(133, 141, 152)
    private val green = Color.rgb(34, 197, 94)
    private val amber = Color.rgb(245, 158, 11)

    private lateinit var root: FrameLayout
    private lateinit var loginButton: Button
    private lateinit var loginError: TextView
    private lateinit var fleetViewModel: FleetViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var dashboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = background
        window.navigationBarColor = background

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(MockAuthRepository())
        )[LoginViewModel::class.java]
        fleetViewModel = ViewModelProvider(
            this,
            FleetViewModelFactory(MockVehicleRepository())
        )[FleetViewModel::class.java]

        root = FrameLayout(this)
        root.setBackgroundColor(background)
        setContentView(root)
        observeState()
        showLogin()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.state.collect { state -> renderLoginState(state) }
                }
                launch {
                    fleetViewModel.state.collect { state ->
                        if (dashboardVisible) renderFleet(state)
                    }
                }
            }
        }
    }

    private fun showLogin() {
        dashboardVisible = false
        root.removeAllViews()

        val scroll = ScrollView(this)
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24))
        }
        scroll.addView(container)

        val brandMark = TextView(this).apply {
            text = "◉"
            textSize = 36f
            setTextColor(blue)
            gravity = Gravity.CENTER
            background = rounded(blue.copy(alpha = 0.12f), 16)
        }
        container.addView(brandMark, widthParams(dp(72), dp(72)).apply {
            topMargin = dp(58)
            bottomMargin = dp(22)
        })

        container.addView(label("BlueTrack", 30f, textPrimary, Typeface.BOLD))
        container.addView(label("Monitoreo GPS para tu operación", 15f, textMuted).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(34))
        })

        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22))
            background = rounded(card, 18, border)
        }
        container.addView(cardLayout, matchParams().apply {
            bottomMargin = dp(18)
        })

        cardLayout.addView(label("Accede a tu flota", 18f, textPrimary, Typeface.BOLD))
        cardLayout.addView(label("Consulta vehículos, dispositivos y alertas en tiempo real", 13f, textMuted).apply {
            setPadding(0, dp(5), 0, dp(20))
        })

        val usernameInput = field("Correo electrónico", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        val passwordInput = field("Contraseña", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        cardLayout.addView(usernameInput, matchParams().apply { bottomMargin = dp(12) })
        cardLayout.addView(passwordInput, matchParams().apply { bottomMargin = dp(14) })

        loginError = label("", 13f, Color.rgb(248, 113, 113)).apply {
            visibility = View.GONE
            setPadding(0, 0, 0, dp(12))
        }
        cardLayout.addView(loginError)

        loginButton = Button(this).apply {
            text = "Iniciar sesión"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(blue, 10)
            setPadding(dp(8))
            setOnClickListener {
                hideKeyboard()
                loginViewModel.login(
                    usernameInput.text.toString(),
                    passwordInput.text.toString()
                )
            }
        }
        cardLayout.addView(loginButton, matchParams().apply { height = dp(50) })

        container.addView(label("BlueStar Technology · systemStar", 12f, textMuted).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        })
        root.addView(scroll, matchParams())
    }

    private fun renderLoginState(state: LoginState) {
        if (state.session?.isAuthenticated == true && !dashboardVisible) {
            dashboardVisible = true
            fleetViewModel.load()
            showDashboard(state.session.user.displayName.orEmpty().ifBlank { state.session.user.username })
            return
        }
        if (!::loginButton.isInitialized) return
        loginButton.isEnabled = !state.isLoading
        loginButton.text = if (state.isLoading) "Validando…" else "Iniciar sesión"
        loginError.text = state.error.orEmpty()
        loginError.visibility = if (state.error.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    private fun showDashboard(displayName: String) {
        root.removeAllViews()
        val shell = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(14))
        }

        val header = LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        brand.addView(label("BlueTrack", 21f, textPrimary, Typeface.BOLD))
        brand.addView(label("CENTRO DE CONTROL", 10f, blue, Typeface.BOLD).apply {
            letterSpacing = 0.14f
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(brand, LinearLayout.LayoutParams(0, dp(50), 1f))
        val logout = Button(this).apply {
            text = "Salir"
            isAllCaps = false
            textSize = 13f
            setTextColor(textMuted)
            background = rounded(Color.TRANSPARENT, 8, border)
            setOnClickListener {
                loginViewModel.logout()
                showLogin()
            }
        }
        header.addView(logout, widthParams(dp(72), dp(42)))
        shell.addView(header)

        shell.addView(label("Hola, $displayName", 25f, textPrimary, Typeface.BOLD).apply {
            setPadding(0, dp(28), 0, dp(4))
        })
        shell.addView(label("Este es el estado de tu operación hoy.", 14f, textMuted).apply {
            setPadding(0, 0, 0, dp(22))
        })

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(content)
        shell.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val stats = LinearLayout(this).apply { gravity = Gravity.CENTER }
        content.addView(stats, matchParams().apply { bottomMargin = dp(24) })
        stats.addView(metricCard("ACTIVOS", "—", blue), LinearLayout.LayoutParams(0, dp(92), 1f).apply {
            rightMargin = dp(6)
        })
        stats.addView(metricCard("EN RUTA", "—", green), LinearLayout.LayoutParams(0, dp(92), 1f).apply {
            leftMargin = dp(3)
            rightMargin = dp(3)
        })
        stats.addView(metricCard("ALERTAS", "0", amber), LinearLayout.LayoutParams(0, dp(92), 1f).apply {
            leftMargin = dp(6)
        })

        val sectionHeader = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        sectionHeader.addView(label("Vehículos", 18f, textPrimary, Typeface.BOLD), LinearLayout.LayoutParams(0, dp(36), 1f))
        val refresh = Button(this).apply {
            text = "Actualizar"
            isAllCaps = false
            textSize = 12f
            setTextColor(blue)
            background = rounded(Color.TRANSPARENT, 8, border)
            setOnClickListener { fleetViewModel.load() }
        }
        sectionHeader.addView(refresh, widthParams(dp(94), dp(38)))
        content.addView(sectionHeader)

        content.addView(label("Ubicación y conectividad de tus unidades", 13f, textMuted).apply {
            setPadding(0, 0, 0, dp(12))
        })
        root.addView(shell, matchParams())
    }

    private fun renderFleet(state: FleetState) {
        val shell = root.getChildAt(0) as? LinearLayout ?: return
        val scroll = shell.getChildAt(2) as? ScrollView ?: return
        val content = scroll.getChildAt(0) as? LinearLayout ?: return
        val stats = content.getChildAt(0) as? LinearLayout ?: return
        val section = content.getChildAt(1) as? LinearLayout ?: return

        if (state.isLoading && state.vehicles.isEmpty()) return
        stats.removeAllViews()
        val active = state.vehicles.count { it.status == "En movimiento" }
        stats.addView(metricCard("ACTIVOS", state.vehicles.size.toString(), blue), LinearLayout.LayoutParams(0, dp(92), 1f).apply { rightMargin = dp(6) })
        stats.addView(metricCard("EN RUTA", active.toString(), green), LinearLayout.LayoutParams(0, dp(92), 1f).apply { leftMargin = dp(3); rightMargin = dp(3) })
        stats.addView(metricCard("ALERTAS", "0", amber), LinearLayout.LayoutParams(0, dp(92), 1f).apply { leftMargin = dp(6) })

        while (content.childCount > 3) content.removeViewAt(3)
        if (state.error != null) {
            content.addView(label(state.error, 14f, Color.rgb(248, 113, 113)).apply { setPadding(0, dp(16), 0, 0) })
            return
        }
        state.vehicles.forEach { vehicle ->
            content.addView(vehicleCard(vehicle), matchParams().apply { topMargin = dp(10) })
        }
    }

    private fun vehicleCard(vehicle: Vehicle): LinearLayout {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16))
            background = rounded(card, 14, border)
        }
        val top = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        val title = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        title.addView(label(vehicle.name, 15f, textPrimary, Typeface.BOLD))
        title.addView(label(vehicle.plate, 12f, textMuted).apply { setPadding(0, dp(3), 0, 0) })
        top.addView(title, LinearLayout.LayoutParams(0, dp(48), 1f))
        val status = TextView(this).apply {
            text = vehicle.status
            textSize = 11f
            setTextColor(if (vehicle.status == "En movimiento") green else amber)
            gravity = Gravity.CENTER
            setPadding(dp(9), dp(6), dp(9), dp(6))
            background = rounded(if (vehicle.status == "En movimiento") Color.rgb(20, 58, 42) else Color.rgb(65, 49, 20), 20)
        }
        top.addView(status)
        wrapper.addView(top)

        wrapper.addView(label(vehicle.location, 13f, textMuted).apply {
            setPadding(0, dp(12), 0, dp(12))
        })
        val details = LinearLayout(this).apply { gravity = Gravity.CENTER_VERTICAL }
        details.addView(label("◉  ${vehicle.speedKmh} km/h", 12f, textPrimary), LinearLayout.LayoutParams(0, dp(28), 1f))
        details.addView(label("▰  ${vehicle.batteryPercent}%", 12f, textMuted).apply { gravity = Gravity.RIGHT })
        wrapper.addView(details)
        wrapper.addView(label(vehicle.lastUpdate, 11f, textMuted).apply { setPadding(0, dp(8), 0, 0) })
        return wrapper
    }

    private fun metricCard(title: String, value: String, accent: Int): LinearLayout {
        val view = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12))
            background = rounded(card, 12, border)
        }
        view.addView(label(title, 10f, textMuted, Typeface.BOLD))
        view.addView(label(value, 26f, accent, Typeface.BOLD).apply { setPadding(0, dp(7), 0, 0) })
        return view
    }

    private fun field(hintText: String, inputTypeValue: Int): EditText = EditText(this).apply {
        hint = hintText
        textSize = 14f
        setTextColor(textPrimary)
        setHintTextColor(textMuted)
        inputType = inputTypeValue
        setSingleLine(true)
        setPadding(dp(14), 0, dp(14), 0)
        background = rounded(Color.rgb(31, 37, 44), 10, border)
        minHeight = dp(50)
    }

    private fun label(textValue: String, size: Float, color: Int, style: Int = Typeface.NORMAL) =
        TextView(this).apply {
            text = textValue
            textSize = size
            setTextColor(color)
            typeface = Typeface.create("sans", style)
        }

    private fun rounded(fill: Int, radius: Int, stroke: Int? = null) =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            stroke?.let { setStroke(dp(1), it) }
        }

    private fun Int.copy(alpha: Float): Int =
        Color.argb((alpha * 255).roundToInt(), Color.red(this), Color.green(this), Color.blue(this))

    private fun matchParams() = LinearLayout.LayoutParams(-1, -2)
    private fun widthParams(width: Int, height: Int) = LinearLayout.LayoutParams(width, height)
    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()

    private fun hideKeyboard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.hideSoftInputFromWindow(root.windowToken, 0)
    }
}