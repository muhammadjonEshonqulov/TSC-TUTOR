package uz.jbnuu.tsc.tutor.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.databinding.ActivityMainBinding
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.ui.location_history.LocationHistoryViewModel
import uz.jbnuu.tsc.tutor.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SendDataToActivity {

    private val vm: LocationHistoryViewModel by viewModels()
    private val timeTest = (6 * 1000).toLong() + 100
    private var timer: CountDownTimer? = null
    private val MYREQUESTCODE = 100

    @Inject
    lateinit var prefs: Prefs

    private var appUpdateManager: AppUpdateManager? = null

    var task: Task<LocationSettingsResponse>? = null
    var locationSettingsRequestBuilder: LocationSettingsRequest.Builder? = null

    lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val REQUEST_CHECK_SETTINGS = 0x1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fetchConfiguration: FetchConfiguration = FetchConfiguration.Builder(this)
            .setDownloadConcurrentLimit(3)
            .build()

        val fetch = getInstance(fetchConfiguration)

        val url = "https://student.jbnuu.uz/rest/v1/student/reference-download?id=2159"
        val file = "/downloads/test.pdf"

        val request = Request(url, file)
        request.priority = (Priority.HIGH)
        request.networkType = (com.tonyodev.fetch2.NetworkType.ALL)
//        request.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ2MVwvYXV0aFwvbG9naW4iLCJhdWQiOiJ2MVwvYXV0aFwvbG9naW4iLCJleHAiOjE2NjUxMTY5MzEsImp0aSI6IjQwMTIwMTEwMDAxMiIsInN1YiI6IjEyIn0.iF5jgbzyACU2wfOU1lEFu0SMkYuIzOpLeCWnJ8v_reE")

        fetch.enqueue(request, { updatedRequest ->
            lg("Success $updatedRequest")
        }) { error ->
            lg("error Main ${error.name}")
        }

        if (appUpdateManager == null) {
            appUpdateManager = AppUpdateManagerFactory.create(this)
        }
        checkUpdate()

//        if (prefs.get(prefs.token, "") == "") {
//            send("Stop")
//        }


        FirebaseMessaging.getInstance().subscribeToTopic("jbnuu_tsc_channel")
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateManager?.registerListener(listener)
                appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, MYREQUESTCODE)
            } else {
                lg("No Update available")
            }
        }
    }

    private val listener: InstallStateUpdatedListener = InstallStateUpdatedListener { installState ->
        if (installState.installStatus() == InstallStatus.DOWNLOADED) {
            lg("An update has been downloaded")
            showSnackBarForCompleteUpdate()
        }
    }

    private fun showSnackBarForCompleteUpdate() {
        val snackbar = Snackbar.make(
            binding.root, "New app is ready!", Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Install") { view: View? ->
            appUpdateManager?.completeUpdate()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(binding.root.context, R.color.cl_color_primary))
        snackbar.show()
    }

    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

    override fun onStop() {
        appUpdateManager?.unregisterListener(listener)
        super.onStop()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    timer?.cancel()
                    timer?.start()
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    snackBar(binding, "Ilovadan foydalanish uchun joylashuvingizni yoqishingizni so'raymiz.")
                    turnOnLocation()
                }
            }
            MYREQUESTCODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        lg("" + "Result Ok")
                    }
                    Activity.RESULT_CANCELED -> {
                        lg("" + "Result Cancelled")
                        checkUpdate()
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        lg("" + "Update Failure")
                        checkUpdate()

                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        send("Stop")
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager?.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, MYREQUESTCODE)
            }
        }

        if (prefs.get(prefs.token, "") != "") {
            send("Start")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    turnOnLocation()
                } else {
                    Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun turnOnLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000

        if (locationSettingsRequestBuilder == null) {
            locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            locationSettingsRequestBuilder?.addLocationRequest(locationRequest)
            locationSettingsRequestBuilder?.setAlwaysShow(true)
        }

        val settingsClient = LocationServices.getSettingsClient(this)
        if (task == null) {
            locationSettingsRequestBuilder?.let {
                task = settingsClient.checkLocationSettings(it.build())
            }
        }

        task?.addOnSuccessListener {
            timer?.cancel()
            timer?.start()
            task = null
            locationSettingsRequestBuilder = null

        }
        task?.addOnFailureListener {
            timer?.cancel()
            if (it is ResolvableApiException) {
                try {
                    val resolvableApiException = it
                    resolvableApiException.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendIntentException: SendIntentException) {
                    sendIntentException.printStackTrace()
                }
            }
            task = null
            locationSettingsRequestBuilder = null
        }
    }

    private fun navigateToLogin() {
        val navControl = findNavController(R.id.nav_host_fragment)
        vm.clearSendLocationBodyData()
        prefs.clear()
        send("Stop")
        if (navControl.navigateUp()) {
            navControl.navigate(R.id.loginFragment)
        }
    }


    private fun sendLocationArray1(sendLocationArrayBody: SendLocationArrayBody) {
        vm.sendLocationArray1(sendLocationArrayBody)
        vm.sendLocationArray1Response.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        vm.clearSendLocationBodyData()
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun sendLocation1(sendLocationBody: SendLocationBody) {
        vm.sendLocation1(sendLocationBody)
        vm.sendLocation1Response.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        navigateToLogin()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }


    override fun send(value: String) {
        if (value == "Start") {
            if (timer == null) {
                timer = object : CountDownTimer(timeTest, 1000) {
                    var time = 0

                    @SuppressLint("SetTextI18n", "VisibleForTests", "SimpleDateFormat")
                    override fun onTick(millisUntilFinished: Long) {
                        if (prefs.get(prefs.loginStop, 0) == 1) {
                            cancel()

                        } else {
                            time = (timeTest - millisUntilFinished).toInt() / 1000
                            if (time == 2) {
                                if (checkPermission()) {
                                    fusedLocationProviderClient = FusedLocationProviderClient(this@MainActivity)
                                    fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val currentDate = sdf.format(Date())
                                        try {
                                            application?.let { appl ->
                                                if (hasInternetConnection(appl)) {
                                                    vm.getSendLocationBodyData()
                                                    vm.getSendLocationsResponse.collectLA(lifecycleScope) { sendLocations ->
                                                        if (sendLocations.isNotEmpty()) {
                                                            sendLocationArray1(SendLocationArrayBody(sendLocations))
                                                        }
                                                    }
                                                    sendLocation1(SendLocationBody(currentDate, "" + it.latitude, "" + it.longitude))

                                                } else {
                                                    vm.insertSendLocationBody(SendLocationBody(currentDate, "" + it.latitude, "" + it.longitude))
                                                }
                                            }

                                        } catch (e: NullPointerException) {
                                            turnOnLocation()
                                        }
                                    }
                                } else {
                                    cancel()
                                }
                            }
                        }
                    }

                    @SuppressLint("SimpleDateFormat")
                    override fun onFinish() {
                        cancel()
                        start()
                    }
                }
            }
            if (checkPermission()) {
                timer?.start()
            }
        } else if (value == "Stop") {
            timer?.cancel()
        }
    }
}