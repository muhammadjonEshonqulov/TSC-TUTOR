package uz.jbnuu.tsc.ui.student_main

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
import uz.jbnuu.tsc.model.history_location.LocationHistoryData
import uz.jbnuu.tsc.model.login.LogoutResponse
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.handleResponse
import uz.jbnuu.tsc.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class StudentMainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _sendLocationResponse = Channel<NetworkResult<SendLocationResponse>>()
    var sendLocationResponse = _sendLocationResponse.receiveAsFlow()

    fun sendLocation(sendLocationBody: SendLocationBody) = viewModelScope.launch {
        _sendLocationResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.sendLocation(sendLocationBody)
                _sendLocationResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _sendLocationResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _sendLocationResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _logoutResponse = Channel<NetworkResult<LogoutResponse>>()
    var logoutResponse = _logoutResponse.receiveAsFlow()

    fun logout() = viewModelScope.launch {
        _logoutResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.logout()
                _logoutResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _logoutResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _logoutResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
//
//    fun clearCategoryData() = viewModelScope.launch {
//        repository.local.clearCategoryData()
//        clearProductsData()
//    }
//
//    private fun clearProductsData() = viewModelScope.launch {
//        repository.local.clearProductsData()
//    }
}