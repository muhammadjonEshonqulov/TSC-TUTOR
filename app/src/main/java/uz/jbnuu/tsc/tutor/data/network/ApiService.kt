package uz.jbnuu.tsc.tutor.data.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import uz.jbnuu.tsc.tutor.model.group.GroupResponse
import uz.jbnuu.tsc.tutor.model.history_location.LocationHistoryResponse
import uz.jbnuu.tsc.tutor.model.login.LogoutResponse
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterBody
import uz.jbnuu.tsc.tutor.model.login.tyuter.LoginTyuterResponse
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationArrayBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationResponse
import uz.jbnuu.tsc.tutor.model.student.PushNotification
import uz.jbnuu.tsc.tutor.model.student.StudentResponse
import uz.jbnuu.tsc.tutor.utils.Constants.Companion.CONTENT_TYPE
import uz.jbnuu.tsc.tutor.utils.Constants.Companion.SERVER_KEY

interface ApiService {

    @POST("login_tyutor")
    suspend fun loginTyuter(@Body loginTyuterBody: LoginTyuterBody): Response<LoginTyuterResponse>

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST
    suspend fun postNotification(@Url full_url: String, @Body notification: PushNotification): Response<ResponseBody>

    @GET("logout")
    suspend fun logout(): Response<LogoutResponse>

    @GET("get_groups")
    suspend fun getGroups(): Response<GroupResponse>

    @GET("get_students")
    suspend fun getStudents(@Query("group_id") group_id: Int?, @Query("key") key: String?, @Query("value") value: String?): Response<StudentResponse>

    @GET("get_history_locations")
    suspend fun getLocationHistory(@Query("student_id") student_id: Int?, @Query("pagination") pagination: Int?, @Query("page") page: Int?): Response<LocationHistoryResponse>

    @POST("send_location1")
    suspend fun sendLocation1(@Body sendLocationBody: SendLocationBody): Response<SendLocationResponse>

    @POST("send_location_array1")
    suspend fun sendLocationArray1(@Body sendLocationArrayBody: SendLocationArrayBody): Response<LogoutResponse>
}