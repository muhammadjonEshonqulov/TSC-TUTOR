package uz.jbnuu.tsc.data

import okhttp3.ResponseBody
import retrofit2.Response
import uz.jbnuu.tsc.data.network.ApiService
import uz.jbnuu.tsc.model.SubjectResponse
import uz.jbnuu.tsc.model.attendance.AttendanceResponse
import uz.jbnuu.tsc.model.group.GroupResponse
import uz.jbnuu.tsc.model.history_location.LocationHistoryBody
import uz.jbnuu.tsc.model.history_location.LocationHistoryResponse
import uz.jbnuu.tsc.model.login.LogoutResponse
import uz.jbnuu.tsc.model.login.hemis.LoginHemisBody
import uz.jbnuu.tsc.model.login.hemis.LoginHemisResponse
import uz.jbnuu.tsc.model.login.student.LoginStudentBody
import uz.jbnuu.tsc.model.login.student.LoginStudentResponse
import uz.jbnuu.tsc.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.model.login.tyuter.LoginTyuterResponse
import uz.jbnuu.tsc.model.me.MeResponse
import uz.jbnuu.tsc.model.performance.PerformanceResponse
import uz.jbnuu.tsc.model.schedule.ScheduleResponse
import uz.jbnuu.tsc.model.semester.SemestersResponse
import uz.jbnuu.tsc.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.model.student.PushNotification
import uz.jbnuu.tsc.model.student.StudentBody
import uz.jbnuu.tsc.model.student.StudentResponse
import uz.jbnuu.tsc.model.subjects.SubjectsResponse
import javax.inject.Inject
import javax.inject.Named

class RemoteDataSource @Inject constructor(@Named("provideApiService") val apiService: ApiService, @Named("provideApiServiceHemis") val apiServiceHemis: ApiService) {

    suspend fun loginStudent(loginStudentBody: LoginStudentBody): Response<LoginStudentResponse> {
        return apiService.loginStudent(loginStudentBody)
    }

    suspend fun loginTyuter(loginTyuterBody: LoginTyuterBody): Response<LoginTyuterResponse> {
        return apiService.loginTyuter(loginTyuterBody)
    }

    suspend fun postNotification(full_url: String, notification: PushNotification): Response<ResponseBody> {
        return apiService.postNotification(full_url, notification)
    }

    suspend fun me(): Response<MeResponse> {
        return apiServiceHemis.me()
    }

    suspend fun loginHemis(loginHemisBody: LoginHemisBody): Response<LoginHemisResponse> {
        return apiServiceHemis.loginHemis(loginHemisBody)
    }

    suspend fun subjects(): Response<SubjectsResponse> {
        return apiServiceHemis.subjects()
    }

    suspend fun subject(subject: Int?, semester: String): Response<SubjectResponse> {
        return apiServiceHemis.subject(subject,semester)
    }

    suspend fun semesters(): Response<SemestersResponse> {
        return apiServiceHemis.semesters()
    }

    suspend fun schedule(week: Int): Response<ScheduleResponse> {
        return apiServiceHemis.schedule(week)
    }

    suspend fun performance(): Response<PerformanceResponse> {
        return apiServiceHemis.performance()
    }

    suspend fun attendance(subject: Int?, semester: Int?): Response<AttendanceResponse> {
        return apiServiceHemis.attendance(subject, semester)
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
        return apiService.getLocationHistory(locationHistoryBody.student_id)
    }

    suspend fun sendLocation(sendLocationBody: SendLocationBody): Response<SendLocationResponse> {
        return apiService.sendLocation(sendLocationBody)
    }

    suspend fun sendLocation1(sendLocationBody: SendLocationBody): Response<SendLocationResponse> {
        return apiService.sendLocation1(sendLocationBody)
    }

    suspend fun sendLocationArray(sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse> {
        return apiService.sendLocationArray(sendLocationArrayBody)
    }

    suspend fun sendLocationArray1(sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse> {
        return apiService.sendLocationArray1(sendLocationArrayBody)
    }

}