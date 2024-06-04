package com.example.composestudy.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composestudy.ui.theme.*
import com.example.composestudy.userInfoProto
import com.example.composestudy.viewmodel.ListViewModel

@Composable
fun ShowTeamDialog(
    onDismiss: () -> Unit,
    onSelectTeam: (String) -> Unit,
    teamList: ArrayList<String>
) {
    val selectedTeam = remember { mutableStateOf("") }

    AlertDialog(
        modifier = Modifier.fillMaxSize(),
        title = {
            Text(text = "Select Team")
        },
        text = {
            Column {
                Text(text = "Team ${selectedTeam.value}")
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(10.dp)
                ) {
                    items(teamList.size) { position ->
                        Text(
                            text = teamList[position],
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    selectedTeam.value = teamList[position]
                                },
                        )
                    }
                }
            }
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(onClick = {
                onSelectTeam(selectedTeam.value)
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(text = "cancel")
            }
        },
    )
}

@Composable
fun msgDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    msg: String,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Alert!!",
                    color = black22,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = msg,
                    color = darkBrown,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, grayEA)
                    ) {
                        Text(text = "cancel", color = black22)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonOK),
                        onClick = { onConfirm() },
                    ) {
                        Text(text = "OK", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun editDialog(
    onDismiss: () -> Unit,
    onConfirm: (userInfoProto) -> Unit,
    userInfo: userInfoProto,
    listViewModel: ListViewModel = hiltViewModel()
) {
    val newName = remember { mutableStateOf(TextFieldValue(userInfo.userName)) }
    val newPosition = remember { mutableStateOf(userInfo.usePosition) }
    val newTeam = remember { mutableStateOf(userInfo.userTeamName) }

    val editUser = userInfoProto.newBuilder()
    editUser.userName = userInfo.userName
    editUser.isOnOff = userInfo.isOnOff
    editUser.userId = userInfo.userId
    editUser.usePosition = userInfo.usePosition
    editUser.userTeamName = userInfo.userTeamName

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Edit User Info",
                    color = black22,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newName.value,
                    onValueChange = { newText ->
                        newName.value = newText
                        editUser.userName = newName.value.text
                    },
                    placeholder = { Text(text = "name", color = grayBB) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomDropDown(
                        defaultValue = newPosition.value,
                        itemList = listViewModel.jobList,
                        onItemSelected = { position ->
                            newPosition.value = position
                            editUser.usePosition = position
                        })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomDropDown(
                        defaultValue = newTeam.value,
                        itemList = listViewModel.teamList,
                        onItemSelected = { team ->
                            newTeam.value = team
                            editUser.userTeamName = team
                        })
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, grayEA)
                    ) {
                        Text(text = "cancel", color = black22)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonOK),
                        onClick = { onConfirm(editUser.build()) },
                    ) {
                        Text(text = "OK", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun customDialog(
    onDismiss: () -> Unit,
    onSelectTeam: (String) -> Unit,
    teamList: ArrayList<String>,
    team: String,
) {
    val selectedTeam = remember { mutableStateOf(team) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Team ${selectedTeam.value}",
                    color = black33,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                ) {
                    items(teamList.size) { position ->
                        Text(
                            text = teamList[position],
                            color = black22,
                            fontSize = 15.sp,
                            modifier = Modifier
                                .padding(10.dp)
                                .clickable {
                                    selectedTeam.value = teamList[position]
                                },
                        )
                        Divider(thickness = 1.dp, color = grayEA)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onDismiss() },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, grayEA)
                    ) {
                        Text(text = "cancel", color = black22)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonOK),
                        onClick = { onSelectTeam(selectedTeam.value) },
                    ) {
                        Text(text = "OK", color = Color.White)
                    }
                }
            }
        }
    }
}