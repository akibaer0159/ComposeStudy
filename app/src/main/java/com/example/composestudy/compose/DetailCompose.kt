package com.example.composestudy.compose

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composestudy.R
import com.example.composestudy.ui.theme.*
import com.example.composestudy.userInfoProto
import com.example.composestudy.viewmodel.DetailViewModel
import com.example.composestudy.viewmodel.ListViewModel

@Composable
fun DetailScreen(
    userId: String,
    detailViewModel: DetailViewModel = hiltViewModel(),
    listViewModel: ListViewModel = hiltViewModel()
) {
    val userInfo: userInfoProto by detailViewModel.getUserInfo(userId)
        .collectAsState(initial = userInfoProto.getDefaultInstance())
    val act = LocalContext.current as Activity

    DetailContent(
        userInfo = userInfo,
        onDeleteClick = {
            listViewModel.deleteUserInfo(userInfo) {
                act.finish()
            }
        },
        onEditClick = { editUserInfo ->
            listViewModel.editUserInfo(editUserInfo)
        },
    )
}

@Composable
fun DetailContent(userInfo: userInfoProto, onDeleteClick: () -> Unit, onEditClick: (userInfoProto) -> Unit) {
    val isShowMsgDialog = remember { mutableStateOf(false) }
    val isShowEditDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Dialog
            ConfirmDialog(isShowMsgDialog, onDeleteClick)
            EditDialog(isShowEditDialog, onEditClick, userInfo)

            Row(
                modifier = Modifier.padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(2f)) {
                    Image(
                        painter = painterResource(id = R.drawable.circle_icons_profile),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = userInfo.userName,
                    color = black22,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(8f)
                )
            }
            Divider(color = darkBrown, thickness = 1.dp)
            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = "Position",
                    color = black22,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = userInfo.usePosition,
                    color = black22,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(8f)
                )
            }
            Divider(color = grayEA, thickness = 1.dp)
            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                Text(
                    text = "Team",
                    color = black22,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(2f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = userInfo.userTeamName,
                    color = black33,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(8f)
                )
            }
            Divider(color = grayEA, thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.padding(vertical = 10.dp)) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        isShowMsgDialog.value = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, grayEA)
                ) {
                    Text(text = "Delete", color = black22)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonOK),
                    onClick = {
                        isShowEditDialog.value = true
                    },
                ) {
                    Text(text = "Edit", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    isShowMsgDialog: MutableState<Boolean>,
    onConfirm: () -> Unit,
) {
    if (isShowMsgDialog.value) {
        msgDialog(
            onDismiss = {
                isShowMsgDialog.value = false
            },
            onConfirm = {
                onConfirm()
                isShowMsgDialog.value = false
            },
            "Delete User?"
        )
    }
}

@Composable
fun EditDialog(
    isShowEditDialog: MutableState<Boolean>,
    onConfirm: (userInfoProto) -> Unit,
    userInfo: userInfoProto,
) {
    if(isShowEditDialog.value) {
        editDialog(
            onDismiss = {
                isShowEditDialog.value = false
            },
            onConfirm = { userInfoProto ->
                onConfirm(userInfoProto)
                isShowEditDialog.value = false
            },
            userInfo = userInfo,
        )
    }
}