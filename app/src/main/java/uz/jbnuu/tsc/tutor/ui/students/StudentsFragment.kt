package uz.jbnuu.tsc.tutor.ui.students

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.adapters.StudentAdapter
import uz.jbnuu.tsc.tutor.base.BaseFragment
import uz.jbnuu.tsc.tutor.databinding.StudentFragmentBinding
import uz.jbnuu.tsc.tutor.model.student.StudentBody
import uz.jbnuu.tsc.tutor.model.student.StudentData
import uz.jbnuu.tsc.tutor.ui.MapsActivity
import uz.jbnuu.tsc.tutor.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class StudentsFragment : BaseFragment<StudentFragmentBinding>(StudentFragmentBinding::inflate), View.OnClickListener, StudentAdapter.OnItemClickListener {
    @Inject
    lateinit var prefs: Prefs
    private val vm: StudentViewModel by viewModels()
    private var group_id: Int? = null
    private var key: String? = null
    private var value: String? = null
    private var scrollState: Parcelable? = null
    private var timer: CountDownTimer? = null
    private val studentsAdapter: StudentAdapter by lazy { StudentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
        arguments?.getInt("group_id", -1)?.let {
            group_id = it
        }
        arguments?.getString("key")?.let {
            key = it
        }
        arguments?.getString("value")?.let {
            value = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listStudents.adapter = null
    }

    private fun getStudentEvery() {
//        if (timer == null) {
        timer = object : CountDownTimer(5000, 500) {
            override fun onTick(millisUntilFinished: Long) {
                lg("on tick mill ->$millisUntilFinished")
                if ((millisUntilFinished / 500).toInt() == 9) {
                    try {
                        binding.listStudents.layoutManager?.onSaveInstanceState()?.let {
                            scrollState = it
                        }
                        if (group_id == -1) {
                            getStudents(StudentBody(null, key, value))
                        } else {
                            getStudents(StudentBody(group_id, key, value))
                        }
                    } catch (e: Exception) {
                        lg("OnTick -> " + e.message.toString())
                    }

                }
            }

            override fun onFinish() {
                try {
                    cancel()
                    start()
                } catch (e: Exception) {
                    lg("Finish -> " + e.message.toString())
                    cancel()
                    start()
                }
            }
        }
//        }
        timer?.cancel()
        timer?.start()

    }

    override fun onResume() {
        super.onResume()
        timer?.start()
        lg("onResume in student")
    }

    override fun onPause() {
        super.onPause()
        lg("onPause in student")
        timer?.cancel()
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
//        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val group_name = arguments?.getString("group_name")
        setupRecycler()
        getStudentEvery()

        when (key) {
            "gender" -> {
                when (value) {
                    "Ayol" -> {
                        binding.titleProfile.text = "Qiz bolalar"
                    }
                    "Erkak" -> {
                        binding.titleProfile.text = "O'g'il bolalar"
                    }
                }
            }
            "type" -> {
                when (value) {
                    "grant" -> {
                        binding.titleProfile.text = "Grantlar"
                    }
                    "kontrakt" -> {
                        binding.titleProfile.text = "Kontraktlar"
                    }
                }
            }
            "social" -> {
                when (value) {
                    "nogiron" -> {
                        binding.titleProfile.text = "Nogironlar"
                    }
                    "yetim" -> {
                        binding.titleProfile.text = "Yetimlar"
                    }
                    "kam" -> {
                        binding.titleProfile.text = "Kam taminlanganlar"
                    }
                }
            }
            else -> {
                if (group_name == null) {
                    binding.titleProfile.text = "Umumiy talabalar ro'yxati"
                } else {
                    binding.titleProfile.text = group_name + " Guruh talabalari"
                }
            }
        }
        binding.backBtn.setOnClickListener(this)
        binding.rotate90.setOnClickListener(this)

        binding.swipeRefreshLayout.setOnRefreshListener {
            getStudentEvery()
//            students()
//            binding.swipeRefreshLayout.isRefreshing = false
//            getStudents(StudentBody(group_id, key, value))
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.backBtn -> {
                finish()
            }
            binding.rotate90 -> {
                if (vm.landscape) {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    vm.landscape = false
                } else {
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    vm.landscape = true
                }
            }
        }
    }

    private fun setupRecycler() {
        binding.listStudents.apply {
            adapter = studentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
//        binding.listStudents.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                scrollState = recyclerView.scrollState
//            }
//
//        })

    }

    private fun getStudents(studentBody: StudentBody) {
        lg("getStudents response")
        vm.getStudent(studentBody)
        vm.getStudentResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            students?.let {
                                if (it.isNotEmpty()) {
                                    binding.listStudentsLay.visibility = View.VISIBLE
                                    binding.notFoundStudent.visibility = View.GONE
                                    studentsAdapter.setData(it)
                                    scrollState?.let {
                                        binding.listStudents.layoutManager?.onRestoreInstanceState(it)
                                    }
                                } else {
                                    binding.listStudentsLay.visibility = View.GONE
                                    binding.notFoundStudent.visibility = View.VISIBLE
                                }
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
                snackBar(binding, "Oxirgi joylashuv mavjud emas")
            }
        } else if (type == 2) {
            if (data.last_location != null) {
                data.id?.let {
                    bundle.putInt("student_id", it)
                    findNavController().navigateSafe(R.id.action_studentsFragment_to_locationHistoryFragment, bundle)
                }
            } else {
                snackBar(binding, "Joylashuvlar tarixi mavjud emas")
            }
        }

    }
}