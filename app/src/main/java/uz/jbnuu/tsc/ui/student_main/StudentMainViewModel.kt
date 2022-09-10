package uz.jbnuu.tsc.ui.student_main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.R
import uz.jbnuu.tsc.app.App
import uz.jbnuu.tsc.data.Repository
import uz.jbnuu.tsc.model.login.LogoutResponse
import uz.jbnuu.tsc.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.model.login.student.LoginStudentResponse
import uz.jbnuu.tsc.model.send_location.SendLocationArrayBody
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

    private val _sendLocationArrayResponse = Channel<NetworkResult<LogoutResponse>>()
    var sendLocationArrayResponse = _sendLocationArrayResponse.receiveAsFlow()

    fun sendLocationArray(sendLocationArrayBody: SendLocationArrayBody) = viewModelScope.launch {
        _sendLocationArrayResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.sendLocationArray(sendLocationArrayBody)
                _sendLocationArrayResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _sendLocationArrayResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _sendLocationArrayResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
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

    fun sendLocation1(sendLocation1Body: SendLocationBody) = viewModelScope.launch {
        _sendLocation1Response.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.sendLocation1(sendLocation1Body)
                _sendLocation1Response.send(handleResponse(response))
            } catch (e: Exception) {
                _sendLocation1Response.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _sendLocation1Response.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _loginResponse = Channel<NetworkResult<LoginStudentResponse>>()
    var loginResponse = _loginResponse.receiveAsFlow()

    fun loginStudent(loginStudentBody: LoginStudentBody) = viewModelScope.launch {
        _loginResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.loginStudent(loginStudentBody)
                _loginResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _loginResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _loginResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
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

    fun insertCategoryData(data: SendLocationBody) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertCategoryData(data)
    }

    private val _getSendLocationsResponse = Channel<List<SendLocationBody>>()
    var getSendLocationsResponse = _getSendLocationsResponse.receiveAsFlow()

    fun getSendLocationBodyData() = viewModelScope.launch(Dispatchers.IO) {
        _getSendLocationsResponse.send(getLocationHistory())
    }

    suspend fun getLocationHistory(): List<SendLocationBody> {
        return repository.local.getSendLocationBodyData().stateIn(viewModelScope).stateIn(viewModelScope).value
    }

    fun clearSendLocationBodyData() = viewModelScope.launch {
        repository.local.clearSendLocationBodyData()
    }
}