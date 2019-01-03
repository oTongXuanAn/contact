package an.xuan.tong.historycontact.api

import an.xuan.tong.historycontact.api.model.*
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*

interface ApiService {
    //?api={api}&phone={phone}
    @GET("/api/Account/UserInfo")
    fun getInfomation(@Query("api") api: String,
                      @Query("phone") phone: String): Observable<InformationResponse>

    //?api={api}&id={id}
    @GET("/api/Account/Retoken")
    fun getRetoken(@Query("api") api: String,
                   @Query("id") id: Int?): Observable<InformationResponse>

    //api/location/insert?api={api}

    @POST("/api/Location/Add")
    fun insertLocation(@Query("api") apikey: String,
                       @Body message: Map<String, String?>): Observable<CallSMSReponse>


    @POST("/api/MessageLog/Add")
    fun insertMessage(@Body message: Map<String, String?>,
                      @Query("api") api: String): Observable<CallSMSReponse>


    @POST("/api/CallLog/Add")
    fun insertCallLog(@Body message: Map<String, String?>,
                      @Query("api") api: String): Observable<CallSMSReponse>

    @POST("/api/Power/Add")
    fun insertPowerLog(@Body message: Map<String, String?>,
                       @Query("api") api: String): Observable<CallSMSReponse>


    @POST("/api/ConnectInternet/Add")
    fun insertInternet(@Body message: Map<String, String?>,
                       @Query("api") api: String): Observable<CallSMSReponse>


    @POST("/api/File/Add")
    @Multipart
    fun insertUpload(
            @Query("api") api: String,
            @Query("id") id: Int? = 0,
            @Part file: MultipartBody.Part): Observable<Array<String>>


}