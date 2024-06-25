package com.example.storyapp.view.main

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.AddStoryActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.adapter.StoryAdapter
import com.example.storyapp.view.map.MapsActivity
import com.example.storyapp.view.model.MainViewModel
import com.example.storyapp.view.model.ViewModelFactory
import com.example.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                setupErrorObserver()
                setupNetworkObserver()
                setupAddStoryButton()
            }
        }

        setupView()
    }

    private fun setupErrorObserver() {
        viewModel.error.observe(this) {
            showToast(it)
        }
    }

    private fun setupNetworkObserver() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @Suppress("DEPRECATION") val activeNetworkInfo = connectivityManager.activeNetworkInfo
        @Suppress("DEPRECATION") val isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting

        viewModel.getSession().observe(this) {
            if (!isConnected) {
                showToast("Tidak ada koneksi internet")
            } else {
                setupRecyclerView()
                viewModel.stories.observe(this) { stories ->
                    (binding.listStory.adapter as StoryAdapter).submitList(stories)
                }
                viewModel.getStory()
            }
        }
    }

    private fun setupAddStoryButton() {
        binding.addFb.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        val adapter = StoryAdapter()
        binding.listStory.layoutManager = LinearLayoutManager(this)
        binding.listStory.adapter = adapter
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                true
            }
            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}