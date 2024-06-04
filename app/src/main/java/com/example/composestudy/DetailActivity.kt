package com.example.composestudy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.composestudy.compose.DetailScreen
import com.example.composestudy.ui.theme.ComposeStudyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra(KEY_USER_ID) ?: ""

        setContent {
            ComposeStudyTheme {
                DetailScreen(userId)
            }
        }
    }

    companion object {
        private const val KEY_USER_ID = "KEY_USER_ID"
        fun newInstance(context: Context, userId: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(KEY_USER_ID, userId)
            return intent
        }
    }
}