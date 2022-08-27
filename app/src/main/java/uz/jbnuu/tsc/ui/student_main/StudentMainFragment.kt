package uz.jbnuu.tsc.ui.student_main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.LogoutDialog
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.StudentMainFragmentBinding
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class StudentMainFragment : BaseFragment<StudentMainFragmentBinding>(StudentMainFragmentBinding::inflate), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private val vm: StudentMainViewModel by viewModels()
    private val timeTest = (6 * 1000).toLong()

    val REQUEST_LOCATION = 199
    private var googleApiClient: GoogleApiClient? = null

    var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var prefs: Prefs


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        enableLoc()
        binding.scheduleLesson.setOnClickListener(this)
        binding.listOfSemester.setOnClickListener(this)
        binding.informationAboutStudy.setOnClickListener(this)
        binding.studentCurriculumVitae.setOnClickListener(this)
        binding.tableOfExams.setOnClickListener(this)
        binding.masteringInSubjects.setOnClickListener(this)
        binding.homeWorksOfLessons.setOnClickListener(this)
        binding.uploadTasks.setOnClickListener(this)
        binding.menu.setOnClickListener(this)
        binding.navView.setNavigationItemSelectedListener(this)



        timer.start()
    }

    private val timer: CountDownTimer = object : CountDownTimer(timeTest, 1000) {     // 601000
        var time = 0

        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            time = (timeTest - millisUntilFinished).toInt() / 1000
//            if (time % 5 == 0){
//                lg()
//            }
            lg("time->$time")
            if (time == 0) {
                fusedLocationProviderClient = FusedLocationProviderClient(requireContext())
                activity?.let {

                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION
                            ), LOCATION_REQUEST_CODE
                        )
                        return
                    }
                }
                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    val sdf = SimpleDateFormat("yyyy-M-dd hh:mm:ss")
                    val currentDate = sdf.format(Date())
                    try {
                        sendLocation(SendLocationBody(it.latitude.toString(), it.longitude.toString(), currentDate))

                    } catch (e: NullPointerException) {
                        enableLoc()
                        snackBar(binding, e.message.toString())
                    }
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        override fun onFinish() {
            this.cancel()
            this.start()
        }
    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lg("resultCode->$resultCode")
        lg("REQUEST_LOCATION->$REQUEST_LOCATION")

        if (resultCode == REQUEST_LOCATION) {

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
    }

    private fun enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(requireContext())
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
                        status.startResolutionForResult(requireActivity(), REQUEST_LOCATION)

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
        vm.sendLocationResponse.collectLatestLA(viewLifecycleOwner) {
            when(it) {
                is NetworkResult.Success -> {
                    it.data?.status?.let {
                        snackBar(binding, "Status -> $it")
                    }
                }
                is NetworkResult.Error -> {
                    if (it.code == 401) {
                        prefs.clear()
                        findNavController().navigateSafe(R.id.action_studentMainFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
                is NetworkResult.Loading -> {

                }
                else -> {}
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.drawer.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        snackBar(binding, "back")
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.scheduleLesson -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.listOfSemester -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.informationAboutStudy -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.studentCurriculumVitae -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.tableOfExams -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.masteringInSubjects -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.homeWorksOfLessons -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.uploadTasks -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.menu -> {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> {
                snackBar(binding, "Profile")
            }
            R.id.settings -> {
                snackBar(binding, "Sozlamalar")
            }
            R.id.logout -> {
                val logoutDialog = LogoutDialog(requireContext())
                logoutDialog.show()
                logoutDialog.setOnCancelClick {
                    logoutDialog.dismiss()
                }
                logoutDialog.setOnSubmitClick {
                    logoutDialog.dismiss()
                    logout()
                }
            }
        }
        binding.drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun logout() {
        vm.logout()
        vm.logoutResponse.collectLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            prefs.clear()
                            findNavController().navigateSafe(R.id.action_studentMainFragment_to_loginFragment)
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        prefs.clear()
                        findNavController().navigateSafe(R.id.action_studentMainFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
                else -> {}
            }
        }
    }

    private fun showLoader() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(binding.root.context, "Iltimos kuting...")
        }
        progressDialog?.show()
    }

    private fun closeLoader() {
        progressDialog?.dismiss()
    }
}