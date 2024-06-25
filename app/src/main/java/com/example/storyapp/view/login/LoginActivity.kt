package com.example.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.storyapp.R
import com.example.storyapp.data.preference.UserModel
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.main.MainActivity
import com.example.storyapp.view.model.LoginViewModel
import com.example.storyapp.view.model.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityLoginBinding

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
        setupView()
        setupAction()
        emailValidation()

        viewModel.loginResponse.observe(this) {
            if (it.error) {
                showToast(it.message)
            } else {
                val email = binding.emailEditText.text.toString()
                val token = it.loginResult.token
                viewModel.saveSession(UserModel(email, token))

                AlertDialog.Builder(this).apply {
                    setTitle("Berhasil Login!")
                    setMessage(it.message)
                    setCancelable(false)
                    val alertDialog = create()
                    alertDialog.show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)

                }
            }
        }
        viewModel.error.observe(this) {
            showToast(it)
        }
        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageview, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val register = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(title, message, email, password, register)
            start()
        }
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

    @SuppressLint("SupportAnnotationUsage")
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditTextLayout.text.toString()
            viewModel.login(email, password)
        }
    }

    private fun emailValidation() {
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()

                binding.emailEditTextLayout.error = when {
                    email.isEmpty() -> {
                        "Email tidak boleh kosong"
                    }

                    !email.contains("@") || !email.contains(".") -> {
                        "Email tidak valid"
                    }

                    else -> {
                        ""
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}