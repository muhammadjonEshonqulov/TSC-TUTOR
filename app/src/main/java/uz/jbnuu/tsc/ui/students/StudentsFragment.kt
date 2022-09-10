package uz.jbnuu.tsc.ui.students

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
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
    private var scrollState: Parcelable? = null
    private var timer: CountDownTimer? = null
    private val studentsAdapter: StudentAdapter by lazy { StudentAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun getStudentEvery() {
        if (timer == null) {
            timer = object : CountDownTimer(5100, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if ((millisUntilFinished / 1000).toInt() == 5) {
                        try {
                            if (group_id == -1) {
                                binding.listStudents.layoutManager?.onSaveInstanceState()?.let {
                                    scrollState = it
                                }
                                getStudents(StudentBody(null, key, value))
                            } else {
                                binding.listStudents.layoutManager?.onSaveInstanceState()?.let {
                                    scrollState = it
                                }
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
        }
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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
        vm.getStudent(studentBody)
        vm.getStudentResponse.collectLatestLA(viewLifecycleOwner) {
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