package com.example.composestudy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composestudy.repo.GeminiRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeminiViewModel @Inject constructor(private val geminiRepo: GeminiRepo) : ViewModel() {
    val generatedMsg: MutableStateFlow<List<String>> = MutableStateFlow(listOf())
    fun getUserInfo(prompt: String) {
        viewModelScope.launch {
            generatedMsg.value += geminiRepo.generateContent(prompt)
            Log.e("kwbae", "list : ${generatedMsg.value}")
        }
    }
}