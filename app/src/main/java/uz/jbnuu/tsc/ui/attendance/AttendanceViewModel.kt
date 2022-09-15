package uz.jbnuu.tsc.ui.attendance

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
import uz.jbnuu.tsc.model.SubjectResponse
import uz.jbnuu.tsc.model.attendance.AttendanceResponse
import uz.jbnuu.tsc.model.login.hemis.LoginHemisBody
import uz.jbnuu.tsc.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.model.semester.SemestersResponse
import uz.jbnuu.tsc.model.subjects.SubjectsResponse
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.handleResponse
import uz.jbnuu.tsc.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _semestersResponse = Channel<NetworkResult<SemestersResponse>>()
    var semestersResponse = _semestersResponse.receiveAsFlow()

    fun semesters() = viewModelScope.launch {
        _semestersResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.semesters()
                _semestersResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _semestersResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _semestersResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _attendanceResponse = Channel<NetworkResult<AttendanceResponse>>()
    var attendanceResponse = _attendanceResponse.receiveAsFlow()

    fun attendance(subject: Int?, semester: Int?) = viewModelScope.launch {
        _attendanceResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.attendance(subject, semester)
                _attendanceResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _attendanceResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _attendanceResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

    private val _subjectsResponse = Channel<NetworkResult<SubjectsResponse>>()
    var subjectsResponse = _subjectsResponse.receiveAsFlow()

    fun subjects() = viewModelScope.launch {
        _subjectsResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.subjects()
                _subjectsResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _subjectsResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _subjectsResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }

//    private val _subjectResponse = Channel<NetworkResult<SubjectResponse>>()
//    var subjectResponse = _subjectResponse.receiveAsFlow()
//
//    fun subject() = viewModelScope.launch {
//        _subjectResponse.send(NetworkResult.Loading())
//        if (hasInternetConnection(getApplication())) {
//            try {
//                val response = repository.remote.subject()
//                _subjectResponse.send(handleResponse(response))
//            } catch (e: Exception) {
//                _subjectResponse.send(NetworkResult.Error("Xatolik : " + e.message))
//            }
//        } else {
//            _subjectResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
//        }
//    }

    private val _loginHemisResponse = Channel<NetworkResult<LoginHemisResponse>>()
    var loginHemisResponse = _loginHemisResponse.receiveAsFlow()

    fun loginHemis(loginHemisBody: LoginHemisBody) = viewModelScope.launch {
        _loginHemisResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.loginHemis(loginHemisBody)
                _loginHemisResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _loginHemisResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _loginHemisResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
}