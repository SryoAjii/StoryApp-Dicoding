package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.database.StoryDatabase
import com.dicoding.picodiploma.loginwithanimation.di.StateResult
import com.dicoding.picodiploma.loginwithanimation.retrofit.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.StoryResponse
import com.dicoding.picodiploma.loginwithanimation.retrofit.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference, private var apiService: ApiService, private var storyDatabase: StoryDatabase
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun userRegister(name: String, email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.register(name, email, password)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun userLogin(email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.login(email, password)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = 5),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStory()
            }
        ).liveData
    }

    fun getLocation() = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.getLocationStories()
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody =e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun getDetail(id: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.getDetail(id)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, DetailResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun uploadImg(imageFile: File, description: String, lat: Double, lon: Double) = liveData {
        emit(StateResult.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImg = imageFile.asRequestBody("image/jpg".toMediaType())
        val requestLat = lat.toString().toRequestBody("text/plain".toMediaType())
        val requestLon = lon.toString().toRequestBody("text/plain".toMediaType())
        val bodyMultipart = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImg
        )
        try {
            val responseSuccess = apiService.upload(bodyMultipart, requestBody, requestLat, requestLon)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(StateResult.Error(responseError.message))
        }
    }

    fun getToken(apiService: ApiService) {
        this.apiService = apiService
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, storyDatabase)
            }.also { instance = it }
    }
}