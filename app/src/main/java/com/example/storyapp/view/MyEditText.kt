package com.example.storyapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R


class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var inputIcon : Drawable? =
        ContextCompat.getDrawable(context, R.drawable.baseline_lock_24)
    private var showPasswordButtonImage =
        ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24) as Drawable
    private var hidePasswordButtonImage: Drawable =
        ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_24) as Drawable
    private var isPasswordVisible: Boolean = false

    init {
        showPasswordButton()
        setOnTouchListener(this)

        background = ContextCompat.getDrawable(context, R.drawable.edittext_border)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.isEmpty()) {
                    showError("Password tidak boleh kosong")
                } else if (password.length < 8) {
                    showError("Password minimal 8 karakter")
                } else {
                    error = null
                }
                if (password.isNotEmpty()) showPasswordButton() else hidePasswordButton()
            }

            override fun afterTextChanged(s: Editable) {
                showPasswordButton()
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showPasswordButton() {
        setButtonDrawables(
            startOfTheText = inputIcon,
            endOfTheText = if (isPasswordVisible) hidePasswordButtonImage else showPasswordButtonImage
        )
    }

    private fun hidePasswordButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null,
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (compoundDrawables[2] != null) {
            val passwordButtonStart: Float
            val passwordButtonEnd: Float
            var isPasswordButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                passwordButtonEnd = (showPasswordButtonImage.intrinsicWidth + paddingStart).toFloat()
                if (event!!.x < passwordButtonEnd) isPasswordButtonClicked = true
            } else {
                passwordButtonStart =
                    (width - paddingEnd - showPasswordButtonImage.intrinsicWidth).toFloat()
                if (event!!.x > passwordButtonStart) isPasswordButtonClicked = true
            }

            if (isPasswordButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPasswordVisible = !isPasswordVisible
                        showPasswordButton()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        inputType = if (isPasswordVisible) {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        } else {
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }
                        setSelection(text?.length ?: 0)
                        showPasswordButton()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun showError(errorMessage: String) {
        error = errorMessage
        invalidate()
    }
}