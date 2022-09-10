package uz.jbnuu.tsc.ui.tutor

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.LogoutDialog
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.HeaderLayoutBinding
import uz.jbnuu.tsc.databinding.TutorsMainPageBinding
import uz.jbnuu.tsc.ui.SendDataToActivity
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.Prefs
import uz.jbnuu.tsc.utils.collectLA
import uz.jbnuu.tsc.utils.navigateSafe
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
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_groupsFragment)
            }
            binding.students -> {
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment)
            }
            binding.contractPaymentAnalysisOfStudents -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.communicationWithStudents -> {
                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.boys -> {
                val bundle = bundleOf("key" to "gender", "value" to "Erkak")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
//                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.girls -> {
                val bundle = bundleOf("key" to "gender", "value" to "Ayol")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
//                snackBar(binding, "Hozirda ishlab chiqishda")
            }
            binding.grands -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val bundle = bundleOf("key" to "type", "value" to "grant")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
            }
            binding.contracts -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val bundle = bundleOf("key" to "type", "value" to "kontrakt")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
            }
            binding.disabled -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val bundle = bundleOf("key" to "social", "value" to "nogiron")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
            }
            binding.lowIncome -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val bundle = bundleOf("key" to "social", "value" to "kam")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
            }
            binding.orphans -> {
//                snackBar(binding, "Hozirda ishlab chiqishda")
                val bundle = bundleOf("key" to "social", "value" to "yetim")
                findNavController().navigateSafe(R.id.action_tutorMainFragment_to_studentsFragment, bundle)
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
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            prefs.clear()
                            sendDataToActivity?.send("Stop")
                            findNavController().navigateSafe(R.id.action_tutorMainFragment_to_loginFragment)
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
                        findNavController().navigateSafe(R.id.action_tutorMainFragment_to_loginFragment)
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