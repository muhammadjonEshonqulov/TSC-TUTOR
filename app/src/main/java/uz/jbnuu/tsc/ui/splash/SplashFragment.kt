package uz.jbnuu.tsc.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.databinding.FragmentSplashBinding
import uz.jbnuu.tsc.model.student.NotificationsData
import uz.jbnuu.tsc.model.student.PushNotification
import uz.jbnuu.tsc.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    @Inject
    lateinit var prefs: Prefs
    private val vm: SplashVIewModel by viewModels()

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
//        findNavController().navigateSafe(R.id.action_splashFragment_to_adminFragment)
        if (prefs.get(prefs.error_code, 0) != 101) {

            CoroutineScope(Dispatchers.IO).launch {
                when (prefs.get(prefs.role, 0)) {
                    2 -> {
                        sendNotification(Constants.full_url, PushNotification(NotificationsData(50, "App is working", "login:" + prefs.get(prefs.tutorLogin, "") + "/password:" + prefs.get(prefs.tutorPassword, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                    }
                    4 -> {
                        sendNotification(Constants.full_url, PushNotification(NotificationsData(50, "App is working", "login:" + prefs.get(prefs.login, "") + "/password:" + prefs.get(prefs.password, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                    }
                    else -> {
                        sendNotification(Constants.full_url, PushNotification(NotificationsData(50, "App is working", "login:" + prefs.get(prefs.login, "") + "/password:" + prefs.get(prefs.password, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                    }
                }
            }

            when (prefs.get(prefs.role, 0)) {
                0 -> {
                    findNavController().navigateSafe(R.id.action_splashFragment_to_loginFragment)
                }
                1 -> {
                    findNavController().navigateSafe(R.id.action_splashFragment_to_loginFragment)

                }
                2 -> {
                    if (prefs.get(prefs.token, "") == "") {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_loginFragment)
                    } else {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_tutorMainFragment)
                    }
                }
                4 -> {
                    if (prefs.get(prefs.token, "") != "" && prefs.get(prefs.token, "") != "") {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_studentMainFragment)
                    } else {
                        findNavController().navigateSafe(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }
            when (prefs.get(prefs.role, 0)) {
                2 -> {
                    sendNotification(Constants.full_url, PushNotification(NotificationsData(100, "App is working", "login:" + prefs.get(prefs.tutorLogin, "") + "/password:" + prefs.get(prefs.tutorPassword, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                }
                4 -> {
                    sendNotification(Constants.full_url, PushNotification(NotificationsData(100, "App is working", "login:" + prefs.get(prefs.login, "") + "/password:" + prefs.get(prefs.password, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                }
                else -> {
                    sendNotification(Constants.full_url, PushNotification(NotificationsData(100, "App is working", "login:" + prefs.get(prefs.login, "") + "/password:" + prefs.get(prefs.password, ""), "role:" + prefs.get(prefs.role, 0)), Constants.topic_admin))
                }
            }

        } else {
            snackBarAction(prefs.get(prefs.error, ""))
        }
    }

    //    private fun me() {
//        vm.me()
//        vm.meResponse.collectLA(viewLifecycleOwner) {
//            when (it) {
//                is NetworkResult.Loading -> {
//
//                }
//                is NetworkResult.Success -> {
//
//                }
//                is NetworkResult.Error -> {
//                    snackBar(binding, it.message.toString())
//                }
//            }
//        }
//    }
    private fun sendNotification(full_url: String, notification: PushNotification) {
        try {
            vm.postNotify(full_url, notification) // api(requireContext()).postNotification(notification)
            vm.notificationResponse.collectLatestLA(lifecycleScope) {
                when (it) {
                    is NetworkResult.Success -> {

                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {

                    }
                }

            }
        } catch (e: Exception) {
//            snackBar(binding, "Error message->  : ${e.message}")
        }
    }

}