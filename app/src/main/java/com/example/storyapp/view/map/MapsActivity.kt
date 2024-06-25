package com.example.storyapp.view.map

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.storyapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.maps_option, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val indo = LatLng(-6.200000, 106.816666)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(indo))

        addManyMarker()
    }
    private fun addManyMarker() {
        val tourismPlace = listOf(
            TourismPlace("Floating Market Lembang", -6.8168954, 107.6151046,"ini lembang uhuyy"),
            TourismPlace("The Great Asia Africa", -6.8331128, 107.6048483,"gimana bingung isis"),
            TourismPlace("Rabbit Town", -6.8668408, 107.608081,"hahahahah submission akhir"),
            TourismPlace("Alun-Alun Kota Bandung", -6.9218518, 107.6025294,"kapan bisa kesini"),
            TourismPlace("Orchid Forest Cikole", -6.780725, 107.637409,"hutan hutan"),
        )

        val boundsBuilder = LatLngBounds.Builder()
        tourismPlace.forEach { data ->
            val latLng = LatLng(data.latitude, data.longitude)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.name)
                    .snippet(data.description)
            )
        }

        try {
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e(TAG, "No points included in the boundsBuilder.")
        }
    }
    data class TourismPlace (
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val description: String
    )
    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
//
//    private fun addManyMarker() {
//        viewModel.stories.observe(this) { stories ->
//            stories.forEach {
//                val latLng = LatLng(it.lat, it.lon)
//                mMap.addMarker(
//                    MarkerOptions()
//                        .position(latLng)
//                        .title(it.name)
//                        .snippet(it.description)
//                )
//            }
//        }
//    }
