package uz.jbnuu.tsc.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import uz.jbnuu.tsc.model.SubjectResponse
import uz.jbnuu.tsc.model.attendance.AttendanceResponse
import uz.jbnuu.tsc.model.group.GroupResponse
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
import uz.jbnuu.tsc.model.student.StudentResponse
import uz.jbnuu.tsc.model.subjects.SubjectsResponse
import uz.jbnuu.tsc.utils.Constants.Companion.CONTENT_TYPE
import uz.jbnuu.tsc.utils.Constants.Companion.SERVER_KEY

interface ApiService {

    @POST("login_student")
    suspend fun loginStudent(@Body loginStudentBody: LoginStudentBody): Response<LoginStudentResponse>

    @POST("login_tyutor")
    suspend fun loginTyuter(@Body loginTyuterBody: LoginTyuterBody): Response<LoginTyuterResponse>

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST
    suspend fun postNotification(@Url full_url: String, @Body notification: PushNotification): Response<ResponseBody>

    @POST("auth/login")
    suspend fun loginHemis(@Body loginHemisBody: LoginHemisBody): Response<LoginHemisResponse>

    @GET("account/me")
    suspend fun me(): Response<MeResponse>

    @GET("education/schedule")
    suspend fun schedule(@Query("week") week: Int): Response<ScheduleResponse>

    @GET("education/subjects")
    suspend fun subjects(): Response<SubjectsResponse>

    @GET("education/subject")
    suspend fun subject(@Query("subject") subject: Int?, @Query("semester") semester: String): Response<SubjectResponse>

    @GET("education/semesters")
    suspend fun semesters(): Response<SemestersResponse>

    @GET("education/performance")
    suspend fun performance(): Response<PerformanceResponse>

    @GET("education/attendance")
    suspend fun attendance(@Query("subject") subject: Int?, @Query("semester") semester: Int?): Response<AttendanceResponse>

    @GET("logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("get_groups")
    suspend fun getGroups(): Response<GroupResponse>

    @GET("get_students")
    suspend fun getStudents(@Query("group_id") group_id: Int?, @Query("key") key: String?, @Query("value") value: String?): Response<StudentResponse>

    @GET("get_history_locations")
    suspend fun getLocationHistory(@Query("student_id") student_id: Int?): Response<LocationHistoryResponse>

    @POST("send_location")
    suspend fun sendLocation(@Body sendLocationBody: SendLocationBody): Response<SendLocationResponse>

    @POST("send_location1")
    suspend fun sendLocation1(@Body sendLocationBody: SendLocationBody): Response<SendLocationResponse>

    @POST("send_location_array")
    suspend fun sendLocationArray(@Body sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse>

    @POST("send_location_array1")
    suspend fun sendLocationArray1(@Body sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse>

//    @POST("orders")
//    suspend fun orders(@Query("table_id") table_id: Int, @Query("waiter_id") waiter_id: Int, @Query("lang") lang: Int, @Body orders: OrderBody): Response<OrderResponse>
}