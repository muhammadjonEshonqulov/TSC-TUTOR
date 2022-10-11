package uz.jbnuu.tsc.tutor.ui.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uz.jbnuu.tsc.tutor.R
import uz.jbnuu.tsc.tutor.tutor.app.App
import uz.jbnuu.tsc.tutor.data.Repository
import uz.jbnuu.tsc.tutor.model.group.GroupResponse
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterResponse
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.handleResponse
import uz.jbnuu.tsc.tutor.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _getGroupsResponse = Channel<NetworkResult<GroupResponse>>()
    var getGroupsResponse = _getGroupsResponse.receiveAsFlow()

    fun getGroups() = viewModelScope.launch {
        _getGroupsResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getGroups()
                _getGroupsResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _getGroupsResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _getGroupsResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _loginTyuterResponse = Channel<NetworkResult<LoginTyuterResponse>>()
    var loginTyuterResponse = _loginTyuterResponse.receiveAsFlow()

    fun loginTyuter(loginTyuterBody: LoginTyuterBody) = viewModelScope.launch {
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
}