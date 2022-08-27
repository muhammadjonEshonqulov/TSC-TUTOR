package uz.jbnuu.tsc.ui.splash

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
import uz.jbnuu.tsc.model.me.MeResponse
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.handleResponse
import uz.jbnuu.tsc.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class SplashVIewModel @Inject constructor(
    val repository: Repository,
    application: Application,
) : AndroidViewModel(application) {

    private val _meResponse = Channel<NetworkResult<MeResponse>>()
    var meResponse = _meResponse.receiveAsFlow()

    fun me() = viewModelScope.launch {
        _meResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.me()
                _meResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _meResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _meResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

}