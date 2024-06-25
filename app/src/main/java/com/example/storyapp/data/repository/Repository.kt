package com.example.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.StoryPagingSource
import com.example.storyapp.data.api.ApiService
import com.example.storyapp.data.preference.Pref
import com.example.storyapp.data.preference.UserModel
import com.example.storyapp.data.respon.DetailStoryResponse
import com.example.storyapp.data.respon.ListStoryItem
import com.example.storyapp.data.respon.LoginResponse
import com.example.storyapp.data.respon.NewStoryResponse
import com.example.storyapp.data.respon.RegisterResponse
import com.example.storyapp.data.respon.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody


class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: Pref,
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

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.addUser(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.loginUser(email, password)
    }

   fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }



    suspend fun getDetailStory(id: String): DetailStoryResponse {
        return apiService.getDetailStory(id)
    }

    suspend fun addStory(photo: MultipartBody.Part, description: RequestBody): NewStoryResponse {
        return apiService.addStory(photo, description)
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: Pref) =
            Repository(apiService, userPreference)
    }
}