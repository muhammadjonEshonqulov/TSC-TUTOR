package uz.jbnuu.tsc.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.databinding.ActivityMapsBinding
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.utils.lg

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var lastLocation: Location
    private var location: SendLocationBody? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

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

            lg("Location : " + location)
            lg("lat : " + location?.lat)
            lg("long : " + location?.long)
            lg("data_time : " + location?.data_time)
        }

//        val location: LocationHistoryData = Gson().fromJson(last_location, LocationHistoryData::class.java)


        enableLoc()
//        checkPermission()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = FusedLocationProviderClient(this)

    }

//    private fun checkPermission(): Boolean {
//        val currentAPIVersion = Build.VERSION.SDK_INT
//        if (currentAPIVersion >= Build.VERSION_CODES.M) {
//            val permissionsNotGranted = ArrayList<String>()
//            for (permission in neededPermissions) {
//                if (ContextCompat.checkSelfPermission(
//                        this,
//                        permission
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    permissionsNotGranted.add(permission)
//                }
//            }
//            if (permissionsNotGranted.size > 0) {
//                var shouldShowAlert = false
//                for (permission in permissionsNotGranted) {
//                    shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(
//                        this,
//                        permission
//                    )
//                }
//
//                val arr = arrayOfNulls<String>(permissionsNotGranted.size)
//                val permissions = permissionsNotGranted.toArray(arr)
//                requestPermissions(permissions, REQUEST_ID_MULTIPLE_PERMISSIONS)
//
//                return false
//            }
//        }
//        return true
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setupMap()
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), LOCATION_REQUEST_CODE
            )
            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val _location = this.location?.lat?.toDouble()?.let { this.location?.long?.toDouble()?.let { it1 -> LatLng(it, it1) } }
                _location?.let {
                    placeMarkerOnMap(_location)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_location, 18f))
                }
            }
        }

    }

    private fun placeMarkerOnMap(currentLatLang: LatLng) {
        val marker = MarkerOptions().position(currentLatLang)
        if (location != null) {
            marker.title("${location?.data_time}.")
            location?.lat?.let { lat ->
                location?.long?.let { long ->
                    lg("id -> " + mMap.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))?.id)
                    lg("id -> " + mMap.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))?.id)
                    lg("id -> " + mMap.addMarker(MarkerOptions().position(LatLng(lat.toDouble(), long.toDouble())))?.id)
//                    mMap.addMarker(MarkerOptions().position(currentLatLang))
                }
            }

        }
    }


    override fun onMarkerClick(p0: Marker): Boolean {
        when(p0.id) {
            "m0" -> {
                p0.title  
            }
        }
        return false
    }

    private fun enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this@MapsActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {}
                    override fun onConnectionSuspended(i: Int) {
                        googleApiClient?.connect()
                    }
                })
                .addOnConnectionFailedListener { connectionResult ->
                    Log.d(
                        "Location error",
                        "Location error " + connectionResult.errorCode
                    )
                }.build()
            googleApiClient?.connect()
            val locationRequest: LocationRequest = LocationRequest.create()
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            locationRequest.setInterval(30 * 1000)
            locationRequest.setFastestInterval(5 * 1000)
            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi.checkLocationSettings(
                    googleApiClient!!,
                    builder.build()
                )
            result.setResultCallback { result ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this@MapsActivity, REQUEST_LOCATION)

                        //                                finish();
                    } catch (e: SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

}