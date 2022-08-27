package uz.jbnuu.tsc.ui.location_history

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.adapters.LocationHistoryAdapter
import uz.jbnuu.tsc.base.BaseFragment
import uz.jbnuu.tsc.base.ProgressDialog
import uz.jbnuu.tsc.databinding.LocationHistoryFragmentBinding
import uz.jbnuu.tsc.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.ui.MapsActivity
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.Prefs
import uz.jbnuu.tsc.utils.collectLA
import uz.jbnuu.tsc.utils.navigateSafe
import javax.inject.Inject

@AndroidEntryPoint
class LocationHistoryFragment : BaseFragment<LocationHistoryFragmentBinding>(LocationHistoryFragmentBinding::inflate), View.OnClickListener, LocationHistoryAdapter.OnItemClickListener {

    private val vm: LocationHistoryViewModel by viewModels()
    var progressDialog: ProgressDialog? = null
    @Inject
    lateinit var prefs: Prefs
    private val locationHistoryAdapter: LocationHistoryAdapter by lazy { LocationHistoryAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val student_id = arguments?.getInt("student_id")
        getLocationHistory(LocationHistoryBody(student_id))
    }

    override fun onViewCreatedd(view: View, savedInstanceState: Bundle?) {

        binding.backBtn.setOnClickListener(this)
        setupRecycler()
    }

    override fun onItemClick(data: LocationHistoryData) {
        val bundle = bundleOf()
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        if (data.lat != null && data.long != null) {
            bundle.putString("last_location", Gson().toJson(SendLocationBody(data.lat, data.long, data.data_time)))
            intent.putExtras(bundle)
            startActivity(intent)
        } else {
            snackBar(binding, "Last location mavjud emas")
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.backBtn -> {
                finish()
            }
        }
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
                                if (it.isEmpty()) {
                                    snackBar(binding, "Ushbu talabada location history mavjud emas")
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
                    if(it.code == 401){
                        prefs.clear()
                        findNavController().navigateSafe(R.id.action_locationHistoryFragment_to_loginFragment)
                    }else{
                        snackBar(binding, it.message.toString())
                    }
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