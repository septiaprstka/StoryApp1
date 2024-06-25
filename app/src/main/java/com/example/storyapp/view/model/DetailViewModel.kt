package com.example.storyapp.view.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.repository.Repository
import com.example.storyapp.data.respon.Story
import kotlinx.coroutines.launch

class DetailViewModel(
    private val Repository: Repository,
) : ViewModel() {

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    fun getStory(id: String) {
        viewModelScope.launch {
            val response = Repository.getDetailStory(id)
            _story.value = (response.story)
        }
    }
}