package uz.jbnuu.tsc.tutor.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.databinding.ActivityMapsBinding
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.utils.lg

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private var location: SendLocationBody? = null
    private var googleApiClient: GoogleApiClient? = null
    val REQUEST_LOCATION = 199

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val last_location: String? = intent?.extras?.getString("last_location")
        last_location?.let {
            location = Gson().fromJson(last_location, SendLocationBody::class.java)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        placeMarkerOnMap()
    }

    @SuppressLint("MissingPermission")
    private fun placeMarkerOnMap() {
        location?.latitude?.let { latitude ->
            location?.longitude?.let { longitude ->
                val marker = MarkerOptions().position(LatLng(latitude.toDouble(), longitude.toDouble()))
                if (location != null) {
                    marker.title("${location?.data_time}.")
                    location?.latitude?.let { lat ->
                        location?.longitude?.let { long ->
                            mMap.isMyLocationEnabled = true
                            mMap.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat.toDouble(), long.toDouble()), 18f))
                        }
                    }

                }
            }
        }

    }

    override fun onMarkerClick(p0: Marker): Boolean {
        when (p0.id) {
            "m0" -> {
                p0.title
            }
        }
        return false
    }
}