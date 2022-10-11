package uz.jbnuu.tsc.tutor.ui.location_history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.adapters.LocationHistoryAdapter
import uz.jbnuu.tsc.tutor.base.BaseFragment
import uz.jbnuu.tsc.tutor.base.ProgressDialog
import uz.jbnuu.tsc.tutor.databinding.LocationHistoryFragmentBinding
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.ui.MapsActivity
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.Prefs
import uz.jbnuu.tsc.tutor.utils.collectLA
import uz.jbnuu.tsc.tutor.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class LocationHistoryFragment : BaseFragment<LocationHistoryFragmentBinding>(LocationHistoryFragmentBinding::inflate), View.OnClickListener, LocationHistoryAdapter.OnItemClickListener {

    private val vm: LocationHistoryViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    private var totalPage = 0
    private var currentPage = 1
    private var student_id = 1
    private var locationHistoryData: ArrayList<LocationHistoryData>? = null

    @Inject
    lateinit var prefs: Prefs
    private val locationHistoryAdapter: LocationHistoryAdapter by lazy { LocationHistoryAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt("student_id")?.let {
            student_id = it
        }

        getLocationHistory(LocationHistoryBody(student_id, null, currentPage))

    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {

        binding.backBtn.setOnClickListener(this)
        setupRecycler()
    }

    override fun onItemClick(data: LocationHistoryData, type: Int) {
        if (type == 1) {
            val bundle = bundleOf()
            val intent = Intent(requireActivity(), MapsActivity::class.java)
            if (data.lat != null && data.long != null) {
                data.data_time?.let {
                    bundle.putString("last_location", Gson().toJson(SendLocationBody(it, data.lat, data.long)))
                }

                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                snackBar(binding, "Last location mavjud emas")
            }
        } else if (type == 2) {
            if (totalPage > 0 && totalPage > currentPage) {
                currentPage++
                getLocationHistory(LocationHistoryBody(student_id, null, currentPage))

            }
        }

    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.backBtn -> {
                finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listLocationHistory.adapter = null
    }

    private fun getLocationHistory(locationHistoryBody: LocationHistoryBody) {
        vm.getLocationHistory(locationHistoryBody)
        vm.getLocationHistoryResponse.collectLA(lifecycleScope) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }
                is NetworkResult.Success -> {
                    closeLoader()
                    if (it.data?.status == 1) {
                        it.data.apply {
                            history?.let {
                                if (locationHistoryData == null) {
                                    locationHistoryData = ArrayList()
                                }
                                if (it.isEmpty()) {
                                    snackBar(binding, "Ushbu talabada joylashuvlar tarixi mavjud emas")
                                } else {
                                    locationHistoryAdapter.setData(it)
                                }
                            }
                        }
                    } else {
                        snackBar(binding, "status " + it.data?.status)
                    }
                }
                is NetworkResult.Error -> {
                    closeLoader()
                    if (it.code == 401) {
                        loginTutor(locationHistoryBody)
                    } else {
                        snackBar(binding, it.message.toString())
                    }
                }
            }
        }
    }

    private fun loginTutor(locationHistoryBody: LocationHistoryBody) {
        vm.loginTutor(LoginTyuterBody(prefs.get(prefs.login, ""), prefs.get(prefs.password, "")))
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
                                prefs.save(prefs.token, it)
                                getLocationHistory(locationHistoryBody)
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

    private fun setupRecycler() {
        binding.listLocationHistory.apply {
            adapter = locationHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}