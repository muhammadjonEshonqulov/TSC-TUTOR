package uz.jbnuu.tsc.tutor.ui.tutor

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.base.BaseFragment
import uz.jbnuu.tsc.tutor.base.LogoutDialog
import uz.jbnuu.tsc.tutor.base.ProgressDialog
import uz.jbnuu.tsc.tutor.databinding.HeaderLayoutBinding
import uz.jbnuu.tsc.tutor.databinding.TutorsMainPageBinding
import uz.jbnuu.tsc.tutor.ui.SendDataToActivity
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.Prefs
import uz.jbnuu.tsc.tutor.utils.collectLA
import uz.jbnuu.tsc.tutor.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class TutorMainFragment : BaseFragment<TutorsMainPageBinding>(TutorsMainPageBinding::inflate), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private val vm: TyuterMainViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    var sendDataToActivity: SendDataToActivity? = null
    var bindNavHeader: HeaderLayoutBinding? = null

    @Inject
    lateinit var prefs: Prefs


    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        bindNavHeader = HeaderLayoutBinding.bind(LayoutInflater.from(requireContext()).inflate(R.layout.header_layout, null, false))
        bindNavHeader?.root?.let { binding.navView.addHeaderView(it) }

        bindNavHeader?.userNameHeader?.text = prefs.get(prefs.fam, "") + " " + prefs.get(prefs.name, "")

        sendDataToActivity?.send("Start")


        onClickListener(this)

    }

    private fun onClickListener(lis: View.OnClickListener) {
        binding.groups.setOnClickListener(lis)
        binding.students.setOnClickListener(lis)
        binding.contractPaymentAnalysisOfStudents.setOnClickListener(lis)
        binding.communicationWithStudents.setOnClickListener(lis)
        binding.boys.setOnClickListener(lis)
        binding.girls.setOnClickListener(lis)
        binding.grands.setOnClickListener(lis)
        binding.contracts.setOnClickListener(lis)
        binding.disabled.setOnClickListener(lis)
        binding.lowIncome.setOnClickListener(lis)
        binding.orphans.setOnClickListener(lis)
        binding.studentDormitory.setOnClickListener(lis)
        binding.rent.setOnClickListener(lis)
        binding.atHome.setOnClickListener(lis)
        binding.menu.setOnClickListener(this)
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.groups -> {
                val extras = FragmentNavigatorExtras(binding.groupsImage to "tutor_to_group")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_groupsFragment, extras = extras)
            }
            binding.students -> {
                val extras = FragmentNavigatorExtras(binding.studentsImage to "tutor_to_students")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, extras = extras)
            }
            binding.contractPaymentAnalysisOfStudents -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.communicationWithStudents -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.boys -> {
                val extras = FragmentNavigatorExtras(binding.boysImage to "tutor_to_students")
                val bundle = bundleOf("key" to "gender", "value" to "Erkak")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.girls -> {
                val extras = FragmentNavigatorExtras(binding.girlsImage to "tutor_to_students")
                val bundle = bundleOf("key" to "gender", "value" to "Ayol")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.grands -> {
                val extras = FragmentNavigatorExtras(binding.grandsImage to "tutor_to_students")
                val bundle = bundleOf("key" to "type", "value" to "grant")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.contracts -> {
                val extras = FragmentNavigatorExtras(binding.contractsImage to "tutor_to_students")
                val bundle = bundleOf("key" to "type", "value" to "kontrakt")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.disabled -> {
                val extras = FragmentNavigatorExtras(binding.disabledImage to "tutor_to_students")
                val bundle = bundleOf("key" to "social", "value" to "nogiron")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.lowIncome -> {
                val extras = FragmentNavigatorExtras(binding.lowIncomeImage to "tutor_to_students")
                val bundle = bundleOf("key" to "social", "value" to "kam")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.orphans -> {
                val extras = FragmentNavigatorExtras(binding.orphansImage to "tutor_to_students")
                val bundle = bundleOf("key" to "social", "value" to "yetim")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle, extras = extras)
            }
            binding.studentDormitory -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.rent -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.atHome -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.menu -> {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.drawer.closeDrawer(GravityCompat.START)
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
                    sendDataToActivity?.send("Stop")
                    prefs.clear()
                    findNavController().navigateSafe(R.id.action_tutorMainFragment_to_loginFragment)
                }
            }
        }
        binding.drawer.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            sendDataToActivity = activity as SendDataToActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity error")
        }
    }

    private fun logout() {
        vm.logout()
        vm.logoutResponse.collectLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
//                    showLoader()
                }
                is NetworkResult.Success -> {
//                    closeLoader()
//                    if (it.data?.status == 1) {
//                        it.data.apply {
//                            prefs.clear()
//                            sendDataToActivity?.send("Stop")
//                            findNavController().navigateSafe(R.id.action_tutorMainFragment_to_loginFragment)
//                        }
//                    } else {
//                        snackBar(binding, "status " + it.data?.status)
//                    }
                }
                is NetworkResult.Error -> {
//                    closeLoader()
//                    if (it.code == 401) {
//                        prefs.clear()
//                        sendDataToActivity?.send("Stop")
//                        findNavController().navigateSafe(R.id.action_tutorMainFragment_to_loginFragment)
//                    } else {
//                        snackBar(binding, it.message.toString())
//                    }
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