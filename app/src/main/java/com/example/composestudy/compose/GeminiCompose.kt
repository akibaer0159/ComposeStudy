package com.example.composestudy.compose

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composestudy.ui.theme.grayBB
import com.example.composestudy.viewmodel.GeminiViewModel

@Composable
fun GeminiScreen(
    geminiViewModel: GeminiViewModel = hiltViewModel(),
) {
    val msgList by geminiViewModel.generatedMsg.collectAsState()
    GeminiContent(msgList, onDonePrompt = { geminiViewModel.getUserInfo(it) })
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GeminiContent(msgList: List<String>, onDonePrompt: (String) -> Unit) {
    val keyboard = LocalSoftwareKeyboardController.current
    var prompt by remember { mutableStateOf("") }

    Scaffold(
        content = {
            Column {
                Box(Modifier.weight(1f)) {
                    LazyColumn {
                        items(msgList.size) {
                            Text(text = msgList[it], modifier = Modifier.padding(10.dp))
                        }
                    }
                }
                Box(Modifier.padding(5.dp)) {
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { value -> prompt = value },
                        placeholder = { Text(text = "Prompt", color = grayBB) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboard?.hide()
                                onDonePrompt(prompt)
                            },
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

        }
    )
}