package uz.jbnuu.tsc.ui.groups

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.adapters.GroupAdapter
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.GroupFragmentBinding
import uz.jbnuu.tsc.model.group.GroupData
import uz.jbnuu.tsc.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.Prefs
import uz.jbnuu.tsc.utils.collectLA
import uz.jbnuu.tsc.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class GroupsFragment : BaseFragment<GroupFragmentBinding>(GroupFragmentBinding::inflate), View.OnClickListener, GroupAdapter.OnItemClickListener {

    private val vm: GroupViewModel by viewModels()

    @Inject
    lateinit var prefs: Prefs
    var progressDialog: ProgressDialog? = null
    private val groupAdapter: GroupAdapter by lazy { GroupAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getGroups()
        sharedElementEnterTransition = MaterialContainerTransform()
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.backBtn.setOnClickListener(this)
        setupRecycler()

        binding.swipeRefreshLayout.setOnRefreshListener {
            getGroups()
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.backBtn -> {
                finish()
            }
        }
    }

    private fun setupRecycler() {
        binding.listGroup.apply {
            adapter = groupAdapter
            postponeEnterTransition()
            layoutManager = GridLayoutManager(requireContext(), 2)
            viewTreeObserver
                .addOnPreDrawListener {
                    startPostponedEnterTransition()
                    true
                }
        }
    }

    private fun getGroups() {
        vm.getGroups()
        vm.getGroupsResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            groups?.let {
                                groupAdapter.setData(it)
                            }
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginTyuter()
//                        prefs.clear()
//                        findNavController().navigateSafe(R.id.action_groupsFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun loginTyuter() {
        vm.loginTyuter(LoginTyuterBody(prefs.get(prefs.tutorLogin, ""), prefs.get(prefs.tutorPassword, "")))
        vm.loginTyuterResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    if (it.data?.status == 1) {
                        it.data.apply {
                            token?.let {
                                prefs.save(prefs.token, it)
                                getGroups()
                            }
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        prefs.clear()
                        findNavController().navigateSafe(R.id.action_groupsFragment_to_loginFragment)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun showLoader() {
        binding.swipeRefreshLayout.isRefreshing = true
//        if (progressDialog == null) {
//            progressDialog = ProgressDialog(binding.root.context, "Iltimos kuting...")
//        }
//        progressDialog?.show()
    }

    private fun closeLoader() {
        binding.swipeRefreshLayout.isRefreshing = false

//        progressDialog?.dismiss()
    }

    override fun onItemClick(data: GroupData, view: ImageView) {
        val extras = FragmentNavigatorExtras(view to "tutor_to_students")

        val bundle = bundleOf("group_id" to data.id, "group_name" to data.name)
        findNavController().navigate(R.id.action_groupsFragment_to_studentsFragment, bundle,  null, extras)
    }
}