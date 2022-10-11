package uz.jbnuu.tsc.tutor.data

import okhttp3.ResponseBody
import retrofit2.Response
import uz.jbnuu.tsc.tutor.data.network.ApiService
import uz.jbnuu.tsc.tutor.model.group.GroupResponse
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryResponse
import uz.jbnuu.tsc.tutor.model.login.LogoutResponse
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterResponse
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.tutor.model.student.PushNotification
import uz.jbnuu.tsc.tutor.model.student.StudentBody
import uz.jbnuu.tsc.tutor.model.student.StudentResponse
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(@Named("provideApiService") val apiService: ApiService, @Named("provideApiServiceHemis") val apiServiceHemis: ApiService) {

    suspend fun loginTyuter(loginTyuterBody: LoginTyuterBody): Response<LoginTyuterResponse> {
        return apiService.loginTyuter(loginTyuterBody)
    }

    suspend fun postNotification(full_url: String, notification: PushNotification): Response<ResponseBody> {
        return apiService.postNotification(full_url, notification)
    }

    suspend fun logout(): Response<LogoutResponse> {
        return apiService.logout()
    }


    suspend fun getGroups(): Response<GroupResponse> {
        return apiService.getGroups()
    }

    suspend fun getStudents(studentBody: StudentBody?): Response<StudentResponse> {
        return apiService.getStudents(studentBody?.group_id, studentBody?.key, studentBody?.value)
    }

    suspend fun getLocationHistory(locationHistoryBody: LocationHistoryBody): Response<LocationHistoryResponse> {
        return apiService.getLocationHistory(locationHistoryBody.student_id, 50, locationHistoryBody.page)
    }

    suspend fun sendLocation1(sendLocationBody: SendLocationBody): Response<SendLocationResponse> {
        return apiService.sendLocation1(sendLocationBody)
    }

    suspend fun sendLocationArray1(sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse> {
        return apiService.sendLocationArray1(sendLocationArrayBody)
    }
}