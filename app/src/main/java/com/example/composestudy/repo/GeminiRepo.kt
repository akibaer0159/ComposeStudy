package com.example.composestudy.repo

import android.content.Context
import android.util.Log
import com.example.composestudy.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GeminiRepo @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun generateContent(inputText: String): String {
        Log.e("kwbae", "inputText: $inputText")
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-pro-latest",
            apiKey = BuildConfig.apiKey
        )

        val inputContent = content {
            text(inputText)
        }

        val response = generativeModel.generateContent(inputContent)
        Log.e("kwbae", "generateContent: ${response.text}")
        return response.text ?: ""
    }
}