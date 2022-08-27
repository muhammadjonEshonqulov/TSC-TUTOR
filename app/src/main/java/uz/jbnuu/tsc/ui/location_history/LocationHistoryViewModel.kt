package uz.jbnuu.tsc.ui.location_history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.app.App
import uz.jbnuu.tsc.data.Repository
import uz.jbnuu.tsc.model.group.GroupResponse
import uz.jbnuu.tsc.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.model.history_location.LocationHistoryResponse
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.handleResponse
import uz.jbnuu.tsc.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class LocationHistoryViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _getLocationHistoryResponse = Channel<NetworkResult<LocationHistoryResponse>>()
    var getLocationHistoryResponse = _getLocationHistoryResponse.receiveAsFlow()

    fun getLocationHistory(locationHistoryBody: LocationHistoryBody) = viewModelScope.launch {
        _getLocationHistoryResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getLocationHistory(locationHistoryBody)
                _getLocationHistoryResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _getLocationHistoryResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _getLocationHistoryResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
}