package uz.jbnuu.tsc.ui.splash

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.databinding.FragmentSplashBinding
import uz.jbnuu.tsc.utils.Prefs
import uz.jbnuu.tsc.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {

    @Inject
    lateinit var prefs: Prefs

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
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


}