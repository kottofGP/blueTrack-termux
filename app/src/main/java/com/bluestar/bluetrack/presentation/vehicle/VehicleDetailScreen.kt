package com.bluestar.bluetrack.presentation.vehicle

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bluestar.bluetrack.domain.model.Vehicle
import kotlin.math.roundToInt

class VehicleDetailScreen(
    private val root: FrameLayout,
    private val vehicle: Vehicle,
    private val onBack: () -> Unit
) {

    private val blue = Color.rgb(0, 124, 255)
    private val background = Color.rgb(15, 17, 21)
    private val card = Color.rgb(24, 28, 32)
    private val border = Color.rgb(34, 40, 47)
    private val textPrimary = Color.rgb(239, 241, 243)
    private val textMuted = Color.rgb(133, 141, 152)
    private val green = Color.rgb(34, 197, 94)
    private val amber = Color.rgb(245, 158, 11)

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

        content.addView(
            vehicleHeader(),
            matchParams().apply {
                bottomMargin = dp(16)
            }
        )

        content.addView(
            informationCard()
        )

        root.addView(
            shell,
            matchParams()
        )
    }

    private fun header(): LinearLayout {
        val header = LinearLayout(root.context).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        val back = TextView(root.context).apply {
            text = "‹  Volver"
            textSize = 14f
            setTextColor(blue)
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(4),
                dp(8),
                dp(12),
                dp(8)
            )

            setOnClickListener {
                onBack()
            }
        }

        header.addView(
            back,
            LinearLayout.LayoutParams(
                0,
                dp(44),
                1f
            )
        )

        header.addView(
            label(
                "DETALLE",
                10f,
                textMuted,
                Typeface.BOLD
            )
        )

        return header
    }

    private fun vehicleHeader(): LinearLayout {
        val wrapper = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18))
            background = rounded(
                card,
                14,
                border
            )
        }

        wrapper.addView(
            label(
                vehicle.name,
                24f,
                textPrimary,
                Typeface.BOLD
            )
        )

        wrapper.addView(
            label(
                vehicle.plate,
                14f,
                textMuted
            ).apply {
                setPadding(
                    0,
                    dp(5),
                    0,
                    dp(16)
                )
            }
        )

        val moving = vehicle.status == "En movimiento"

        val status = TextView(root.context).apply {
            text = vehicle.status
            textSize = 12f

            setTextColor(
                if (moving) green else amber
            )

            gravity = Gravity.CENTER
            setPadding(
                dp(10),
                dp(7),
                dp(10),
                dp(7)
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

        wrapper.addView(
            status,
            LinearLayout.LayoutParams(
                -2,
                -2
            )
        )

        return wrapper
    }

    private fun informationCard(): LinearLayout {
        val cardView = LinearLayout(root.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18))
            background = rounded(
                card,
                14,
                border
            )
        }

        cardView.addView(
            label(
                "Información actual",
                17f,
                textPrimary,
                Typeface.BOLD
            )
        )

        cardView.addView(
            detailRow(
                "Ubicación",
                vehicle.location
            )
        )

        cardView.addView(
            detailRow(
                "Velocidad",
                "${vehicle.speedKmh} km/h"
            )
        )

        cardView.addView(
            detailRow(
                "Batería",
                "${vehicle.batteryPercent}%"
            )
        )

        cardView.addView(
            detailRow(
                "Última actualización",
                vehicle.lastUpdate
            )
        )

        return cardView
    }

    private fun detailRow(
        title: String,
        value: String
    ): LinearLayout {
        val row = LinearLayout(root.context).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                0,
                dp(14),
                0,
                dp(14)
            )
        }

        row.addView(
            label(
                title,
                13f,
                textMuted
            ),
            LinearLayout.LayoutParams(
                0,
                -2,
                1f
            )
        )

        row.addView(
            label(
                value,
                13f,
                textPrimary,
                Typeface.BOLD
            )
        )

        return row
    }

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

    private fun matchParams() =
        LinearLayout.LayoutParams(
            -1,
            -2
        )

    private fun dp(value: Int) =
        (
            value *
                root.context.resources.displayMetrics.density
        ).roundToInt()
}
