package com.example.composestudy.data

import androidx.compose.ui.graphics.Color

data class UserInfo(
    val userName: String = "",
    val userPosition: String = "",
    val isOnOff: Boolean = false,
    val userTeamName: String = "",
    val userId: String = "",
) {
    val onOffColor: Color
        get() {
            return if (isOnOff) {
                Color.Green
            } else {
                Color.Red
            }
        }
}