package uz.jbnuu.tsc.ui

import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.databinding.ActivityMainBinding
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.ui.login.LoginFragment
import uz.jbnuu.tsc.ui.student_main.StudentMainViewModel
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.collectLA
import uz.jbnuu.tsc.utils.lg
import uz.jbnuu.tsc.utils.snackBar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val vm: StudentMainViewModel by viewModels()
    private val timeTest = (6 * 1000).toLong()

    val REQUEST_LOCATION = 199
    private var googleApiClient: GoogleApiClient? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
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
                        status.startResolutionForResult(this, REQUEST_LOCATION)

                        //                                finish();
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }


    private fun sendLocation(sendLocationBody: SendLocationBody) {
        vm.sendLocation(sendLocationBody)
        vm.sendLocationResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }
}