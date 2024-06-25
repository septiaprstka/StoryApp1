package com.example.storyapp.data.api

import com.example.storyapp.data.respon.DetailStoryResponse
import com.example.storyapp.data.respon.LoginResponse
import com.example.storyapp.data.respon.NewStoryResponse
import com.example.storyapp.data.respon.RegisterResponse
import com.example.storyapp.data.respon.StoryResponse
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
    suspend fun addUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(position: Int, loadSize: Int): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id: String,
    ): DetailStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): NewStoryResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): StoryResponse
}
