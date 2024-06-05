package com.dicoding.picodiploma.loginwithanimation.view.map

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    fun getLocation() = repository.getLocation()
}