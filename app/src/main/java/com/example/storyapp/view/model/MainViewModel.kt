package com.example.storyapp.view.model


import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.preference.UserModel
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.respon.ListStoryItem
import com.example.storyapp.data.respon.NewStoryResponse
import com.example.storyapp.data.respon.Story
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class MainViewModel (private val repository: Repository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getStories() {
        viewModelScope.launch {
            repository.getStories()
        }
    }
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    fun getStory(id: String) {
        viewModelScope.launch {
            val response = repository.getDetailStory(id)
            _story.value = (response.story)
        }
    }

    private val _addResponse = MutableLiveData<NewStoryResponse>()
    val addResponse: LiveData<NewStoryResponse> = _addResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun addStory(photo: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.addStory(photo, description)
                _addResponse.value = response

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, NewStoryResponse::class.java)
                _error.value = errorResponse.message
            }
            _isLoading.value = false
        }
    }
}