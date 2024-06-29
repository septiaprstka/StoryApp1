package com.example.storyapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.model.RegisterViewModel
import com.example.storyapp.view.model.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()
        setupAction()
        nameValidation()
        emailValidation()

        viewModel.registerResponse.observe(this) { response ->
            if (response.error) {
                showToast(response.message)
            } else {
                showRegistrationDialog(response .message)
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            showToast(errorMessage)
        }
    }

    private fun showRegistrationDialog(message: String) {
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("Register Akun!")
            setMessage(message)
            setCancelable(false)
        }.create()

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 2000)
    }

    private fun playAnimation() {
        val imageAnimator = ObjectAnimator.ofFloat(binding.imageview, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val titleAnimator = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val nameAnimator = ObjectAnimator.ofFloat(binding.nameeditTextLayout, View.ALPHA, 1f).setDuration(200)
        val emailAnimator = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordAnimator = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val registerAnimator = ObjectAnimator.ofFloat(binding.signupbutton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(titleAnimator, nameAnimator, emailAnimator, passwordAnimator, registerAnimator)
            start()
        }

        imageAnimator.start()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupbutton.setOnClickListener {
            val name = binding.nameeditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()

            val isNameValid = binding.nameeditTextLayout.error
            val isEmailValid = binding.emailEditTextLayout.error
            val isPasswordValid = binding.passwordEditTextLayout.error

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast("Silahkan isi semua data")
            } else if (isNameValid != null || isEmailValid != null || isPasswordValid != null) {
                showToast("Lengkapi data yang anda isi")
            } else {
                viewModel.register(name, email, password)
            }
        }
    }

    private fun nameValidation() {
        binding.nameeditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString()

                binding.nameeditTextLayout.error = when {
                    name.isEmpty() -> "Nama tidak boleh kosong"
                    name.length < 5 -> "Nama minimal 5 karakter"
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun emailValidation() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()

                binding.emailEditTextLayout.error = when {
                    email.isEmpty() -> "Email tidak boleh kosong"
                    !email.contains("@") || !email.contains(".") -> "Email tidak valid"
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}