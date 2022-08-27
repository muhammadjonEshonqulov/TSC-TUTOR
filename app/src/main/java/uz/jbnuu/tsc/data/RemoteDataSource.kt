package uz.jbnuu.tsc.data

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
import uz.jbnuu.tsc.model.semester.SemestersResponse
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import uz.jbnuu.tsc.model.send_location.SendLocationResponse
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

    suspend fun me(): Response<MeResponse> {
        return apiServiceHemis.me()
    }

    suspend fun loginHemis(loginHemisBody: LoginHemisBody): Response<LoginHemisResponse> {
        return apiServiceHemis.loginHemis(loginHemisBody)
    }

    suspend fun subjects(): Response<SubjectsResponse> {
        return apiServiceHemis.subjects()
    }

    suspend fun subject(): Response<SubjectResponse> {
        return apiServiceHemis.subject()
    }

    suspend fun semesters(): Response<SemestersResponse> {
        return apiServiceHemis.semesters()
    }

    suspend fun performance(): Response<PerformanceResponse> {
        return apiServiceHemis.performance()
    }

    suspend fun attendance(): Response<AttendanceResponse> {
        return apiServiceHemis.attendance()
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

}