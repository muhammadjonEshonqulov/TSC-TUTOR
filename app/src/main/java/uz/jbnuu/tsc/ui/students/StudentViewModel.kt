package uz.jbnuu.tsc.ui.students

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
import uz.jbnuu.tsc.model.student.StudentBody
import uz.jbnuu.tsc.model.student.StudentResponse
import uz.jbnuu.tsc.utils.NetworkResult
import uz.jbnuu.tsc.utils.handleResponse
import uz.jbnuu.tsc.utils.hasInternetConnection
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _getStudentResponse = Channel<NetworkResult<StudentResponse>>()
    var getStudentResponse = _getStudentResponse.receiveAsFlow()

    fun getStudent(studentBody: StudentBody? = null) = viewModelScope.launch {
        _getStudentResponse.send(NetworkResult.Loading())
        if (hasInternetConnection(getApplication())) {
            try {
                val response = repository.remote.getStudents(studentBody)
                _getStudentResponse.send(handleResponse(response))
            } catch (e: Exception) {
                _getStudentResponse.send(NetworkResult.Error("Xatolik : " + e.message))
            }
        } else {
            _getStudentResponse.send(NetworkResult.Error(App.context.getString(R.string.connection_error)))
        }
    }
}