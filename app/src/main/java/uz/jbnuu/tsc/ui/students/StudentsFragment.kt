package uz.jbnuu.tsc.ui.students

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.adapters.StudentAdapter
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.StudentFragmentBinding
import uz.jbnuu.tsc.model.student.StudentBody
import uz.jbnuu.tsc.model.student.StudentData
import uz.jbnuu.tsc.ui.MapsActivity
import uz.jbnuu.tsc.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class StudentsFragment : BaseFragment<StudentFragmentBinding>(StudentFragmentBinding::inflate), View.OnClickListener, StudentAdapter.OnItemClickListener {
    @Inject
    lateinit var prefs: Prefs
    private val vm: StudentViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    private var group_id: Int? = null
    private var key: String? = null
    private var value: String? = null
    private val studentsAdapter: StudentAdapter by lazy { StudentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt("group_id")?.let {
            group_id = it
        }
        arguments?.getString("key")?.let {
            key = it
        }
        arguments?.getString("value")?.let {
            value = it
        }
        getStudents(StudentBody(group_id, key, value))
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val group_name = arguments?.getString("group_name")
        if (group_name == null) {
            binding.titleProfile.text = "Umumiy lalabalari ro'yxati"
        } else {
            binding.titleProfile.text = group_name + " Guruh talabalari"
        }
        binding.backBtn.setOnClickListener(this)
        setupRecycler()
        binding.swipeRefreshLayout.setOnRefreshListener {
            getStudents(StudentBody(group_id, key, value))
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
        binding.listStudents.apply {
            adapter = studentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getStudents(studentBody: StudentBody) {
        vm.getStudent(studentBody)
        vm.getStudentResponse.collectLatestLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            students?.let {
                                studentsAdapter.setData(it)
                            }
//                            groups?.let {
//                                studentsAdapter.setData(it)
//                            }
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        prefs.clear()
                        findNavController().navigateSafe(R.id.action_studentsFragment_to_loginFragment)
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


    override fun onItemClick(data: StudentData, type: Int) {
        val bundle = bundleOf()
        if (type == 1) {
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            if (data.last_location != null) {
                bundle.putString("last_location", data.last_location)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                snackBar(binding, "Last location mavjud emas")
            }
        } else if (type == 2) {

            data.id?.let {
                bundle.putInt("student_id", it)
                findNavController().navigateSafe(R.id.action_studentsFragment_to_locationHistoryFragment, bundle)
            }
        }

    }
}