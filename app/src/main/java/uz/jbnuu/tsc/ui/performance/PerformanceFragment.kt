package uz.jbnuu.tsc.ui.performance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.adapters.PerformanceAdapter
import uz.jbnuu.tsc.adapters.SemesterAdapter
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.databinding.PerformanceFragmentBinding
import uz.jbnuu.tsc.model.login.hemis.LoginHemisBody
import uz.jbnuu.tsc.model.performance.PerformanceData
import uz.jbnuu.tsc.model.semester.SemestersData
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.Prefs
import uz.jbnuu.tsc.utils.collectLA
import uz.jbnuu.tsc.utils.lg
import javax.inject.Inject

@AndroidEntryPoint
class PerformanceFragment : BaseFragment<PerformanceFragmentBinding>(PerformanceFragmentBinding::inflate), SemesterAdapter.OnItemClickListener, PerformanceAdapter.OnItemClickListener {

    private val vm: PerfoemanceViewModel by viewModels()
    private val semesterAdapter: SemesterAdapter by lazy { SemesterAdapter(this) }
    private val performanceAdapter: PerformanceAdapter by lazy { PerformanceAdapter(this) }

    private var performanceData: ArrayList<PerformanceData>? = null

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performance()
        semesters()
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {
        setupSemestersList()
        setupPerformanceList()
        binding.group.text = prefs.get(prefs.group, "")
        binding.swipeRefreshLayout.isRefreshing = false
        binding.swipeRefreshLayout.isEnabled = false
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


    private fun semesters() {
        vm.semesters()
        vm.semestersResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            semesterAdapter.setData(it)
                        }

                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginHemis("semesters")
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun performance() {
        vm.performance()
        vm.performanceResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.success == true) {
                        it.data.data?.let {
                            if (performanceData == null) {
                                performanceData = ArrayList()
                            }
                            performanceData?.clear()
                            performanceData?.addAll(it)
                        }

                    } else {
                        snackBar(binding, "Hemis " + it.data?.error)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginHemis("semesters")
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun loginHemis(s: String) {
        vm.loginHemis(LoginHemisBody(prefs.get(prefs.login, ""), prefs.get(prefs.password, "")))
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
                                semesters()
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

    private fun setupSemestersList() {
        binding.listSemester.apply {
            adapter = semesterAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupPerformanceList() {
        binding.listPerformance.apply {
            adapter = performanceAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showLoader() {
        binding.swipeRefreshLayout.isRefreshing = true
    }

    private fun closeLoader() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onItemClick(data: SemestersData, position: Int, type: Int) {
        if (type == 1) {
            semesterAdapter.dataProduct.forEachIndexed { index, semestersData ->
                semestersData.currentExtra = index == position
            }
            lg("semesterAdapter->" + semesterAdapter.dataProduct)
            semesterAdapter.notifyDataSetChanged()
        }
        val performances = ArrayList<PerformanceData>()
        performances.clear()
        performanceData?.forEach {
            if (it._semester == data.code){
                performances.add(it)
            }
        }
        if (performances.size == 0){
            binding.listPerformanceLay.visibility = View.GONE
            binding.notFoundLesson.visibility = View.VISIBLE
        } else {
            performanceAdapter.setData(performances)
            binding.listPerformanceLay.visibility = View.VISIBLE
            binding.notFoundLesson.visibility = View.GONE
        }
    }

    override fun onItemClick(data: PerformanceData, type: Int) {

    }
}