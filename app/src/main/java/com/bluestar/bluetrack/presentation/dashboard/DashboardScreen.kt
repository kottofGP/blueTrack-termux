package com.bluestar.bluetrack.presentation.dashboard

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bluestar.bluetrack.domain.model.Vehicle
import com.bluestar.bluetrack.presentation.fleet.FleetScreen
import com.bluestar.bluetrack.presentation.fleet.FleetState
import kotlin.math.roundToInt

class DashboardScreen(
    private val root: FrameLayout,
    private val displayName: String,
    private val onLogout: () -> Unit,
    private val onRefresh: () -> Unit,
    private val onVehicleSelected: (Vehicle) -> Unit
) {

    private val blue = Color.rgb(0, 124, 255)
    private val background = Color.rgb(15, 17, 21)
    private val card = Color.rgb(24, 28, 32)
    private val border = Color.rgb(34, 40, 47)
    private val textPrimary = Color.rgb(239, 241, 243)
    private val textMuted = Color.rgb(133, 141, 152)
    private val green = Color.rgb(34, 197, 94)
    private val amber = Color.rgb(245, 158, 11)

    private lateinit var stats: LinearLayout
    private lateinit var fleetContainer: LinearLayout
    private lateinit var fleetScreen: FleetScreen

    fun show() {
        root.removeAllViews()

        val shell = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(20),
                dp(22),
                dp(20),
                dp(14)
            )
        }

        shell.addView(header())

        shell.addView(
            label(
                "Hola, $displayName",
                25f,
                textPrimary,
                Typeface.BOLD
            ).apply {
                setPadding(
                    0,
                    dp(28),
                    0,
                    dp(4)
                )
            }
        )

        shell.addView(
            label(
                "Este es el estado de tu operación hoy.",
                14f,
                textMuted
            ).apply {
                setPadding(
                    0,
                    0,
                    0,
                    dp(22)
                )
            }
        )

        val scroll = ScrollView(root.context)

        val content = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(content)

        shell.addView(
            scroll,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        stats = LinearLayout(root.context).apply {
            gravity = Gravity.CENTER
        }

        content.addView(
            stats,
            LinearLayout.LayoutParams(
                -1,
                -2
            ).apply {
                bottomMargin = dp(24)
            }
        )

        fleetContainer = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            fleetContainer,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        fleetScreen = FleetScreen(
            container = fleetContainer,
            onRefresh = onRefresh,
            onVehicleSelected = onVehicleSelected
        )

        fleetScreen.show()

        root.addView(
            shell,
            LinearLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    fun render(state: FleetState) {
        if (!::stats.isInitialized || !::fleetScreen.isInitialized) {
            return
        }

        if (state.isLoading && state.vehicles.isEmpty()) {
            return
        }

        val active = state.vehicles.count {
            it.status == "En movimiento"
        }

        stats.removeAllViews()

        stats.addView(
            metricCard(
                "ACTIVOS",
                state.vehicles.size.toString(),
                blue
            ),
            metricParams().apply {
                rightMargin = dp(6)
            }
        )

        stats.addView(
            metricCard(
                "EN RUTA",
                active.toString(),
                green
            ),
            metricParams().apply {
                leftMargin = dp(3)
                rightMargin = dp(3)
            }
        )

        stats.addView(
            metricCard(
                "ALERTAS",
                "0",
                amber
            ),
            metricParams().apply {
                leftMargin = dp(6)
            }
        )

        fleetScreen.render(state)
    }

    private fun header(): LinearLayout {
        val header = LinearLayout(root.context).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        brand.addView(
            label(
                "BlueTrack",
                21f,
                textPrimary,
                Typeface.BOLD
            )
        )

        brand.addView(
            label(
                "CENTRO DE CONTROL",
                10f,
                blue,
                Typeface.BOLD
            ).apply {
                letterSpacing = 0.14f
                setPadding(
                    0,
                    dp(3),
                    0,
                    0
                )
            }
        )

        header.addView(
            brand,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            )
        )

        val logout = Button(root.context).apply {
            text = "Salir"
            isAllCaps = false
            textSize = 13f
            setTextColor(textMuted)
            background = rounded(
                Color.TRANSPARENT,
                8,
                border
            )

            setOnClickListener {
                onLogout()
            }
        }

        header.addView(
            logout,
            LinearLayout.LayoutParams(
                dp(72),
                dp(42)
            )
        )

        return header
    }

    private fun metricCard(
        title: String,
        value: String,
        accent: Int
    ): LinearLayout {
        val view = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12))
            background = rounded(
                card,
                12,
                border
            )
        }

        view.addView(
            label(
                title,
                10f,
                textMuted,
                Typeface.BOLD
            )
        )

        view.addView(
            label(
                value,
                26f,
                accent,
                Typeface.BOLD
            ).apply {
                setPadding(
                    0,
                    dp(7),
                    0,
                    0
                )
            }
        )

        return view
    }

    private fun metricParams() =
        LinearLayout.LayoutParams(
            0,
            dp(92),
            1f
        )

    private fun label(
        textValue: String,
        size: Float,
        color: Int,
        style: Int = Typeface.NORMAL
    ) =
        TextView(root.context).apply {
            text = textValue
            textSize = size
            setTextColor(color)
            typeface = Typeface.create(
                "sans",
                style
            )
        }

    private fun rounded(
        fill: Int,
        radius: Int,
        stroke: Int? = null
    ) =
        GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()

            stroke?.let {
                setStroke(dp(1), it)
            }
        }

    private fun dp(value: Int) =
        (
            value *
                root.context.resources.displayMetrics.density
        ).roundToInt()
}
