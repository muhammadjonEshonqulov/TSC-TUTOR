package uz.jbnuu.tsc.tutor.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import uz.jbnuu.tsc.tutor.data.Repository
import uz.jbnuu.tsc.tutor.utils.NetworkResult
import uz.jbnuu.tsc.tutor.utils.handleResponse
import uz.jbnuu.tsc.tutor.utils.hasInternetConnection
import uz.jbnuu.tsc.tutor.model.student.PushNotification
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
) : AndroidViewModel(application) {


    private val _notificationResponse = Channel<NetworkResult<ResponseBody>>()
    var notificationResponse = _notificationResponse.receiveAsFlow()

    fun postNotify(full_url: String, notification: PushNotification) = viewModelScope.launch {
        _notificationResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.postNotification(full_url, notification)
                _notificationResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _notificationResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _notificationResponse.send(NetworkResult.Error("Server bilan aloqa yo'q"))
        }
    }
}