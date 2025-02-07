package com.dicoding.picodiploma.loginwithanimation.retrofit.api

import com.dicoding.picodiploma.loginwithanimation.retrofit.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetail(
        @Path("id") id: String
    ) : DetailResponse

    @Multipart
    @POST("stories")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ) : UploadResponse

    @GET("stories")
    suspend fun getLocationStories(
        @Query("location") location: Int = 1
    ) : StoryResponse

}