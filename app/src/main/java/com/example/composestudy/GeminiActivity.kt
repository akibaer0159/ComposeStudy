package com.example.composestudy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.composestudy.compose.GeminiScreen
import com.example.composestudy.ui.theme.ComposeStudyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeminiActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeStudyTheme {
                GeminiScreen()
            }
        }
    }

    companion object {
        fun newInstance(context: Context): Intent {
            val intent = Intent(context, GeminiActivity::class.java)
            return intent
        }
    }
}