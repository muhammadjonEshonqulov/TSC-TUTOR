package uz.jbnuu.tsc.ui.login

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.LoginFragmentBinding
import uz.jbnuu.tsc.model.login.hemis.LoginHemisBody
import uz.jbnuu.tsc.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.utils.*
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

        val spinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.array, R.layout.item_spinner_login)
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_login)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                whichOne = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.loginBtn -> {
                hideKeyboard()
                val login = binding.loginAuth.text.toString()
                val password = binding.passwordAuth.text.toString()
                if (login.isNotEmpty() && password.isNotEmpty()) {
//                    login(LoginBody(login, password))
                    when (whichOne) {
//                        0 -> {
//                            snackBar(binding, "Hozirda admin ro'li mavjud emas.")
//                            // login(LoginBody(login, password))
//                        }
                        0 -> {
                            loginTyuter(LoginTyuterBody(login, password))
                        }
                        1 -> {
                            loginHemis(LoginHemisBody(login, password))
                        }
                    }

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

    private fun loginHemis(loginHemisBody: LoginHemisBody) {
        vm.loginHemis(loginHemisBody)
        vm.loginHemisResponse.collectLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    if (it.data?.success == true) {
                        it.data.apply {

                            data?.token?.let {
                                prefs.save(prefs.hemisToken, it)
                                loginStudent(LoginStudentBody(loginHemisBody.login, loginHemisBody.password, it))
                            }
                        }
                    } else {
                        it.data?.error?.let {
                            snackBar(binding, " " + it)
                        }
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    snackBar(binding, it.message.toString())
                }
            }
        }
    }

    private fun loginStudent(loginStudentBody: LoginStudentBody) {
        vm.loginStudent(loginStudentBody)
        vm.loginResponse.collectLA(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            prefs.save(prefs.login, "${loginStudentBody.login}")
                            prefs.save(prefs.password, "${loginStudentBody.password}")
                            token?.let {
                                prefs.save(prefs.token, it)
                            }
                            hemins_token?.let {
                                prefs.save(prefs.hemisToken, it)
                            }
                            prefs.save(prefs.role, 4)
                            meHemis()
//                            findNavController().navigateSafe(R.id.action_loginFragment_to_studentMainFragment)
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


    private fun meHemis() {
        vm.meHemis()
        vm.meHemisResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.apply {
                            first_name?.let {
                                prefs.save(prefs.name, it)
                            }
                            second_name?.let {
                                prefs.save(prefs.fam, it)
                            }
                            group?.name?.let {
                                prefs.save(prefs.group, it)
                            }
                            image?.let {
                                prefs.save(prefs.image, it)
                            }
                            findNavController().navigateSafe(R.id.action_loginFragment_to_studentMainFragment)
                        }
                    } else {
                        snackBar(binding, "error " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    snackBar(binding, it.message.toString())
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
                                prefs.save(prefs.tutorLogin, "${loginTyuterBody.login}")
                                prefs.save(prefs.tutorPassword, "${loginTyuterBody.password}")
                                prefs.save(prefs.token, it)
                            }
                            familya?.let {
                                prefs.save(prefs.fam, it)
                            }
                            ism?.let {
                                prefs.save(prefs.name, it)
                            }
                            role_id?.let {
                                prefs.save(prefs.role, it)
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