package uz.jbnuu.tsc.tutor.ui.tutor

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
import uz.jbnuu.tsc.tutor.model.login.LogoutResponse
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.handleResponse
import uz.jbnuu.tsc.tutor.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class TyuterMainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

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