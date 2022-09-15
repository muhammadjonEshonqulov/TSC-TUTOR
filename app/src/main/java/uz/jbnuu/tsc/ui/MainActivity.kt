package uz.jbnuu.tsc.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.databinding.ActivityMainBinding
import uz.jbnuu.tsc.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.model.subjects.SubjectsData
import uz.jbnuu.tsc.ui.student_main.StudentMainViewModel
import uz.jbnuu.tsc.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SendDataToActivity {

    private val vm: StudentMainViewModel by viewModels()
    private val timeTest = (6 * 1000).toLong()
    private var timer: CountDownTimer? = null

    @Inject
    lateinit var prefs: Prefs

    val REQUEST_LOCATION = 199

    lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val REQUEST_CHECK_SETTINGS = 0x1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        MobileAds.initialize(this) {
//
//        }

//        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        FirebaseMessaging.getInstance().subscribeToTopic("jbnuu_tsc_channel")
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }


    private fun checkPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

    //    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
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
        }
    }

    override fun onPause() {
        super.onPause()
        when (prefs.get(prefs.role, 0)) {
            2 -> {
                if (prefs.get(prefs.token, "") != "") {
                    send("Stop")
                }
            }
            4 -> {
                if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.token, "") != "") {
                    send("Stop")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (prefs.get(prefs.role, 0)) {
            2 -> {
                if (prefs.get(prefs.token, "") != "") {
                    send("Start")
                }
            }
            4 -> {
                if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.token, "") != "") {
                    send("Start")
                }
            }
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

        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        locationSettingsRequestBuilder.addLocationRequest(locationRequest)
        locationSettingsRequestBuilder.setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build()) as Task<LocationSettingsResponse>

        task.addOnSuccessListener {
            timer?.cancel()
            timer?.start()
        }
        task.addOnFailureListener {
            timer?.cancel()
            if (it is ResolvableApiException) {
                try {
                    val resolvableApiException = it
                    resolvableApiException.startResolutionForResult(this@MainActivity, REQUEST_CHECK_SETTINGS)
                } catch (sendIntentException: SendIntentException) {
                    sendIntentException.printStackTrace()
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
//                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun sendLocationArray(sendLocationArrayBody: SendLocationArrayBody) {
        vm.sendLocationArray(sendLocationArrayBody)
        vm.sendLocationArrayResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        vm.clearSendLocationBodyData()
//                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun subjects() {
        vm.subjects()
        vm.subjectsResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.data?.let {
                        val currentSemesterSubjects = ArrayList<SubjectsData>()
                        currentSemesterSubjects.clear()
                        it.forEachIndexed { index, subjectsData ->
                            if (prefs.get(prefs.semester, "") == subjectsData._semester) {
                                currentSemesterSubjects.add(subjectsData)
                                subject(subjectsData.subject?.id, subjectsData._semester)
                            }
                        }

                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun subject(subject: Int?, semester: String) {
        vm.subject(subject, semester)
        vm.subjectResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {

                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun sendLocationArray1(sendLocationArrayBody: SendLocationArrayBody) {
        vm.sendLocationArray1(sendLocationArrayBody)
        vm.sendLocationArray1Response.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        vm.clearSendLocationBodyData()
//                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
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
//                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        login()
                    }
                }
                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun login() {
        prefs.save(prefs.loginStop, 1)
        vm.loginStudent(LoginStudentBody(prefs.get(prefs.login, ""), prefs.get(prefs.password, ""), prefs.get(prefs.token, "")))
        vm.loginResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.token?.let {
                        prefs.save(prefs.loginStop, 0)
                        prefs.save(prefs.token, it)
                        timer?.start()

                        subjects()
                    }
                }
                is NetworkResult.Error -> {

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
                                        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                                        val currentDate = sdf.format(Date())
                                        try {
                                            application?.let { appl ->
                                                if (hasInternetConnection(appl)) {
                                                    vm.getSendLocationBodyData()
                                                    vm.getSendLocationsResponse.collectLA(lifecycleScope) { sendLocations ->
                                                        if (sendLocations.isNotEmpty()) {
                                                            if (prefs.get(prefs.role, 0) == 4) {
                                                                sendLocationArray(SendLocationArrayBody(sendLocations))
                                                            } else if (prefs.get(prefs.role, 0) == 2) {
                                                                sendLocationArray1(SendLocationArrayBody(sendLocations))
                                                            }
                                                        }
                                                    }
                                                    if (prefs.get(prefs.role, 0) == 4) {
                                                        sendLocation(SendLocationBody(currentDate, "" + it.latitude, "" + it.longitude))
                                                    } else if (prefs.get(prefs.role, 0) == 2) {
                                                        sendLocation1(SendLocationBody(currentDate, "" + it.latitude, "" + it.longitude))
                                                    } else {

                                                    }
                                                } else {
                                                    vm.insertCategoryData(SendLocationBody(currentDate, "" + it.latitude, "" + it.longitude))
                                                }
                                            }

                                        } catch (e: NullPointerException) {
                                            turnOnLocation()
//                                        snackBar(binding, "location error -> " + e.message.toString())
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
                turnOnLocation()
            }
        } else if (value == "Stop") {
            timer?.cancel()
        }
    }
}