package com.bluestar.bluetrack.presentation.login

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import kotlin.math.roundToInt

class LoginScreen(
    private val context: Context,
    private val root: FrameLayout,
    private val onLogin: (String, String) -> Unit
) {

    private val blue = Color.rgb(0, 124, 255)
    private val background = Color.rgb(15, 17, 21)
    private val card = Color.rgb(24, 28, 32)
    private val border = Color.rgb(34, 40, 47)
    private val textPrimary = Color.rgb(239, 241, 243)
    private val textMuted = Color.rgb(133, 141, 152)

    private lateinit var loginButton: Button
    private lateinit var loginError: TextView

    fun show() {
        root.removeAllViews()

        val scroll = ScrollView(context)

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24))
        }

        scroll.addView(container)

        val brandMark = TextView(context).apply {
            text = "◉"
            textSize = 36f
            setTextColor(blue)
            gravity = Gravity.CENTER
            background = rounded(blue.copyAlpha(0.12f), 16)
        }

        container.addView(
            brandMark,
            widthParams(dp(72), dp(72)).apply {
                topMargin = dp(58)
                bottomMargin = dp(22)
            }
        )

        container.addView(
            label("BlueTrack", 30f, textPrimary, Typeface.BOLD)
        )

        container.addView(
            label("Monitoreo GPS para tu operación", 15f, textMuted).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(6), 0, dp(34))
            }
        )

        val cardLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22))
            background = rounded(card, 18, border)
        }

        container.addView(
            cardLayout,
            matchParams().apply {
                bottomMargin = dp(18)
            }
        )

        cardLayout.addView(
            label("Accede a tu flota", 18f, textPrimary, Typeface.BOLD)
        )

        cardLayout.addView(
            label(
                "Consulta vehículos, dispositivos y alertas en tiempo real",
                13f,
                textMuted
            ).apply {
                setPadding(0, dp(5), 0, dp(20))
            }
        )

        val usernameInput = field(
            "Correo electrónico",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )

        val passwordInput = field(
            "Contraseña",
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        cardLayout.addView(
            usernameInput,
            matchParams().apply {
                bottomMargin = dp(12)
            }
        )

        cardLayout.addView(
            passwordInput,
            matchParams().apply {
                bottomMargin = dp(14)
            }
        )

        loginError = label(
            "",
            13f,
            Color.rgb(248, 113, 113)
        ).apply {
            visibility = View.GONE
            setPadding(0, 0, 0, dp(12))
        }

        cardLayout.addView(loginError)

        loginButton = Button(context).apply {
            text = "Iniciar sesión"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(blue, 10)
            setPadding(dp(8))

            setOnClickListener {
                hideKeyboard()

                onLogin(
                    usernameInput.text.toString(),
                    passwordInput.text.toString()
                )
            }
        }

        cardLayout.addView(
            loginButton,
            matchParams().apply {
                height = dp(50)
            }
        )

        container.addView(
            label(
                "BlueStar Technology · systemStar",
                12f,
                textMuted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(12), 0, 0)
            }
        )

        root.addView(scroll, matchParams())
    }

    fun renderState(state: LoginState) {
        if (!::loginButton.isInitialized) return

        loginButton.isEnabled = !state.isLoading

        loginButton.text =
            if (state.isLoading) {
                "Validando…"
            } else {
                "Iniciar sesión"
            }

        loginError.text = state.error.orEmpty()

        loginError.visibility =
            if (state.error.isNullOrBlank()) {
                View.GONE
            } else {
                View.VISIBLE
            }
    }

    private fun field(
        hintText: String,
        inputTypeValue: Int
    ): EditText =
        EditText(context).apply {
            hint = hintText
            textSize = 14f
            setTextColor(textPrimary)
            setHintTextColor(textMuted)
            inputType = inputTypeValue
            setSingleLine(true)
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(
                Color.rgb(31, 37, 44),
                10,
                border
            )
            minHeight = dp(50)
        }

    private fun label(
        textValue: String,
        size: Float,
        color: Int,
        style: Int = Typeface.NORMAL
    ) =
        TextView(context).apply {
            text = textValue
            textSize = size
            setTextColor(color)
            typeface = Typeface.create("sans", style)
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

    private fun Int.copyAlpha(alpha: Float): Int =
        Color.argb(
            (alpha * 255).roundToInt(),
            Color.red(this),
            Color.green(this),
            Color.blue(this)
        )

    private fun matchParams() =
        LinearLayout.LayoutParams(-1, -2)

    private fun widthParams(
        width: Int,
        height: Int
    ) =
        LinearLayout.LayoutParams(width, height)

    private fun dp(value: Int) =
        (value * context.resources.displayMetrics.density).roundToInt()

    private fun hideKeyboard() {
        (
            context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as? InputMethodManager
        )?.hideSoftInputFromWindow(root.windowToken, 0)
    }
}
