package com.bluestar.bluetrack.presentation.fleet

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.bluestar.bluetrack.domain.model.Vehicle
import kotlin.math.roundToInt

class FleetScreen(
    private val container: LinearLayout,
    private val onRefresh: () -> Unit,
    private val onVehicleSelected: (Vehicle) -> Unit
) {

    private val blue = Color.rgb(0, 124, 255)
    private val card = Color.rgb(24, 28, 32)
    private val border = Color.rgb(34, 40, 47)
    private val textPrimary = Color.rgb(239, 241, 243)
    private val textMuted = Color.rgb(133, 141, 152)
    private val green = Color.rgb(34, 197, 94)
    private val amber = Color.rgb(245, 158, 11)

    fun show() {
        container.removeAllViews()

        val header = LinearLayout(container.context).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(
            label(
                "Vehículos",
                18f,
                textPrimary,
                Typeface.BOLD
            ),
            LinearLayout.LayoutParams(
                0,
                dp(36),
                1f
            )
        )

        val refresh = android.widget.Button(container.context).apply {
            text = "Actualizar"
            isAllCaps = false
            textSize = 12f
            setTextColor(blue)
            background = rounded(
                Color.TRANSPARENT,
                8,
                border
            )

            setOnClickListener {
                onRefresh()
            }
        }

        header.addView(
            refresh,
            LinearLayout.LayoutParams(
                dp(94),
                dp(38)
            )
        )

        container.addView(header)

        container.addView(
            label(
                "Ubicación y conectividad de tus unidades",
                13f,
                textMuted
            ).apply {
                setPadding(0, 0, 0, dp(12))
            }
        )
    }

    fun render(state: FleetState) {
        if (state.isLoading && state.vehicles.isEmpty()) {
            return
        }

        while (container.childCount > 2) {
            container.removeViewAt(2)
        }

        if (state.error != null) {
            container.addView(
                label(
                    state.error,
                    14f,
                    Color.rgb(248, 113, 113)
                ).apply {
                    setPadding(0, dp(16), 0, 0)
                }
            )
            return
        }

        state.vehicles.forEach { vehicle ->
            container.addView(
                vehicleCard(vehicle),
                LinearLayout.LayoutParams(
                    -1,
                    -2
                ).apply {
                    topMargin = dp(10)
                }
            )
        }
    }

    private fun vehicleCard(vehicle: Vehicle): LinearLayout {
        val wrapper = LinearLayout(container.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16))
            background = rounded(
                card,
                14,
                border
            )

            isClickable = true
            isFocusable = true

            setOnClickListener {
                onVehicleSelected(vehicle)
            }
        }

        val top = LinearLayout(container.context).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = LinearLayout(container.context).apply {
            orientation = LinearLayout.VERTICAL
        }

        title.addView(
            label(
                vehicle.name,
                15f,
                textPrimary,
                Typeface.BOLD
            )
        )

        title.addView(
            label(
                vehicle.plate,
                12f,
                textMuted
            ).apply {
                setPadding(0, dp(3), 0, 0)
            }
        )

        top.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                dp(48),
                1f
            )
        )

        val moving = vehicle.status == "En movimiento"

        val status = TextView(container.context).apply {
            text = vehicle.status
            textSize = 11f
            setTextColor(
                if (moving) green else amber
            )
            gravity = Gravity.CENTER
            setPadding(
                dp(9),
                dp(6),
                dp(9),
                dp(6)
            )
            background = rounded(
                if (moving) {
                    Color.rgb(20, 58, 42)
                } else {
                    Color.rgb(65, 49, 20)
                },
                20
            )
        }

        top.addView(status)
        wrapper.addView(top)

        wrapper.addView(
            label(
                vehicle.location,
                13f,
                textMuted
            ).apply {
                setPadding(
                    0,
                    dp(12),
                    0,
                    dp(12)
                )
            }
        )

        val details = LinearLayout(container.context).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        details.addView(
            label(
                "◉  ${vehicle.speedKmh} km/h",
                12f,
                textPrimary
            ),
            LinearLayout.LayoutParams(
                0,
                dp(28),
                1f
            )
        )

        details.addView(
            label(
                "▰  ${vehicle.batteryPercent}%",
                12f,
                textMuted
            ).apply {
                gravity = Gravity.RIGHT
            }
        )

        wrapper.addView(details)

        wrapper.addView(
            label(
                vehicle.lastUpdate,
                11f,
                textMuted
            ).apply {
                setPadding(0, dp(8), 0, 0)
            }
        )

        return wrapper
    }

    private fun label(
        textValue: String,
        size: Float,
        color: Int,
        style: Int = Typeface.NORMAL
    ) =
        TextView(container.context).apply {
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
                container.context.resources.displayMetrics.density
        ).roundToInt()
}
