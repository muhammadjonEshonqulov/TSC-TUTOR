package uz.jbnuu.tsc.tutor.ui.login

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.base.BaseFragment
import uz.jbnuu.tsc.tutor.base.ProgressDialog
import uz.jbnuu.tsc.tutor.databinding.LoginFragmentBinding
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginFragmentBinding>(LoginFragmentBinding::inflate), View.OnClickListener {

    private val vm: LoginViewModel by viewModels()
    var progressDialog: ProgressDialog? = null

    @Inject
    lateinit var prefs: Prefs

    var whichOne = 0

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.loginBtn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.loginBtn -> {
                hideKeyboard()
                val login = binding.loginAuth.text.toString()
                val password = binding.passwordAuth.text.toString()
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    loginTyuter(LoginTyuterBody(login, password))
                } else {
                    if (login.isEmpty()) {
                        binding.loginAuth.error = "Loginingizni kiriting"
                    }
                    if (password.isEmpty()) {
                        binding.passwordAuth.error = "Passwordni kiriting"
                    }
                }
            }
        }
    }


    private fun loginTyuter(loginTyuterBody: LoginTyuterBody) {
        vm.loginTyuter(loginTyuterBody)
        vm.loginTyuterResponse.collectLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            token?.let {
                                prefs.save(prefs.login, "${loginTyuterBody.login}")
                                prefs.save(prefs.password, "${loginTyuterBody.password}")
                                prefs.save(prefs.token, it)
                            }
                            familya?.let {
                                prefs.save(prefs.fam, it)
                            }
                            ism?.let {
                                prefs.save(prefs.name, it)
                            }
                            role_id?.let {
                                if (it == 2) {
                                    findNavController().navigateSafe(R.id.action_loginFragment_to_tutorMainFragment)
                                } else {
                                    snackBar(binding, "Noto'g'ri rol ")
                                }
                            }
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    snackBar(binding, it.message.toString())
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