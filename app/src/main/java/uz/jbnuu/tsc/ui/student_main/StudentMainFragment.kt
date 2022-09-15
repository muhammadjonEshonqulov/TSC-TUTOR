package uz.jbnuu.tsc.ui.student_main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.LogoutDialog
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.HeaderLayoutBinding
import uz.jbnuu.tsc.databinding.StudentMainFragmentBinding
import uz.jbnuu.tsc.ui.SendDataToActivity
import uz.jbnuu.tsc.utils.*
import javax.inject.Inject


@AndroidEntryPoint
class StudentMainFragment :
    BaseFragment<StudentMainFragmentBinding>(StudentMainFragmentBinding::inflate),
    View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    var sendDataToActivity: SendDataToActivity? = null

    lateinit var bindNavHeader: HeaderLayoutBinding

    private val vm: StudentMainViewModel by viewModels()

    var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var prefs: Prefs

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            sendDataToActivity = activity as SendDataToActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity error")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) {

        }

//        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder()
            .build()
        binding.adView.loadAd(adRequest)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SourceLockedOrientationActivity", "SetTextI18n")
    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

//        MobileAds.initialize(requireContext()) {}
//        val adView = AdView(requireContext())
//
//        adView.adSize = AdSize.BANNER
//
//        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

        bindNavHeader = HeaderLayoutBinding.bind(
            LayoutInflater.from(requireContext()).inflate(R.layout.header_layout, null, false)
        )
        binding.navView.addHeaderView(bindNavHeader.root)
        bindNavHeader.userNameHeader.text =
            prefs.get(prefs.fam, " ") + " " + prefs.get(prefs.name, "")
        bindNavHeader.organizationUserHeader.text = prefs.get(prefs.group, "") + " guruh"

        prefs.get(prefs.image, "").let {
            Glide.with(requireContext())
                .load(it)
                .placeholder(R.drawable.logo_main)
                .into(bindNavHeader.imgUserDrawer)
        }

        sendDataToActivity?.send("Start")
        binding.scheduleLesson.setOnClickListener(this)
        binding.attendance.setOnClickListener(this)
        binding.informationAboutStudy.setOnClickListener(this)
        binding.studentCurriculumVitae.setOnClickListener(this)
        binding.tableOfExams.setOnClickListener(this)
        binding.performance.setOnClickListener(this)
        binding.homeWorksOfLessons.setOnClickListener(this)
        binding.uploadTasks.setOnClickListener(this)
        binding.menu.setOnClickListener(this)
        binding.navView.setNavigationItemSelectedListener(this)


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
                val extras = FragmentNavigatorExtras(binding.scheduleLessonImage to "schedule_lesson_image")

                findNavController().navigateSafe(
                    R.id.action_studentMainFragment_to_scheduleFragment, null, null, extras
                )
            }
            binding.attendance -> {
                val extras = FragmentNavigatorExtras(binding.attendanceImage to "attendance_image")

                findNavController().navigateSafe(R.id.action_studentMainFragment_to_attendanceFragment, extras = extras)
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
            binding.performance -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val extras = FragmentNavigatorExtras(binding.performanceImage to "performance_image")
                findNavController().navigateSafe(R.id.action_studentMainFragment_to_performanceFragment, extras = extras)
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
                            sendDataToActivity?.send("Stop")
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
                        sendDataToActivity?.send("Stop")
                        findNavController().navigateSafe(R.id.action_studentMainFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
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