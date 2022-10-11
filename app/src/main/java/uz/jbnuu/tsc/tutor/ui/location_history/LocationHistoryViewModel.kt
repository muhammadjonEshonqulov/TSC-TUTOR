package uz.jbnuu.tsc.tutor.ui.location_history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.data.Repository
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryResponse
import uz.jbnuu.tsc.tutor.model.login.LogoutResponse
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterResponse
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.tutor.tutor.app.App
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.handleResponse
import uz.jbnuu.tsc.tutor.utils.hasInternetConnection
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

    private val _loginTyuterResponse = Channel<NetworkResult<LoginTyuterResponse>>()
    var loginTyuterResponse = _loginTyuterResponse.receiveAsFlow()

    fun loginTutor(loginTyuterBody: LoginTyuterBody) = viewModelScope.launch {
        _loginTyuterResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.loginTyuter(loginTyuterBody)
                _loginTyuterResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _loginTyuterResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _loginTyuterResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _sendLocationArray1Response = Channel<NetworkResult<LogoutResponse>>()
    var sendLocationArray1Response = _sendLocationArray1Response.receiveAsFlow()

    fun sendLocationArray1(sendLocationArrayBody: SendLocationArrayBody) = viewModelScope.launch {
        _sendLocationArray1Response.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.sendLocationArray1(sendLocationArrayBody)
                _sendLocationArray1Response.send(handleResponse(response))
            } catch (e: Exception) {
                _sendLocationArray1Response.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _sendLocationArray1Response.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _sendLocation1Response = Channel<NetworkResult<SendLocationResponse>>()
    var sendLocation1Response = _sendLocation1Response.receiveAsFlow()

    fun sendLocation1(sendLocationBody: SendLocationBody) = viewModelScope.launch {
        _sendLocation1Response.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.sendLocation1(sendLocationBody)
                _sendLocation1Response.send(handleResponse(response))
            } catch (e: Exception) {
                _sendLocation1Response.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _sendLocation1Response.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _getSendLocationsResponse = Channel<List<SendLocationBody>>()
    var getSendLocationsResponse = _getSendLocationsResponse.receiveAsFlow()

    fun getSendLocationBodyData() = viewModelScope.launch(Dispatchers.IO) {
        _getSendLocationsResponse.send(getLocationHistory())
    }

    suspend fun getLocationHistory(): List<SendLocationBody> {
        return repository.local.getSendLocationBodyData().stateIn(viewModelScope).value
    }

    fun clearSendLocationBodyData() = viewModelScope.launch {
        repository.local.clearSendLocationBodyData()
    }

    fun insertSendLocationBody(data: SendLocationBody) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertSendLocationBody(data)
    }
}