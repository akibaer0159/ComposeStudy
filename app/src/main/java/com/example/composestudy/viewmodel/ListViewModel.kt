package com.example.composestudy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composestudy.data.UserInfo
import com.example.composestudy.repo.UserInfoRepo
import com.example.composestudy.userInfoProto
import com.example.composestudy.userListProto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class ListViewModel @Inject constructor(private val userInfoRepo: UserInfoRepo) :
    ViewModel() {
    val name = MutableLiveData("")
    val nameList = MutableLiveData<ArrayList<UserInfo>>(arrayListOf())
    val jobList = arrayListOf("Android", "iOS", "Server", "Design", "Operator", "Leader")
    val job = MutableLiveData(jobList.first())
    val teamList = arrayListOf("Dev", "Manage", "Test")
    val team = MutableLiveData(teamList.first())
    val userDataStore: Flow<userListProto> = userInfoRepo.userListDataStore

    fun onNameChanged(newName: String) {
        name.value = newName
    }

    fun onInputName() {
        val userinfo = UserInfo(
            userName = name.value ?: "",
            userPosition = job.value ?: "",
            isOnOff = Random.nextBoolean(),
            userTeamName = team.value ?: "",
            userId = System.currentTimeMillis().toString(),
        )
        nameList.value?.add(
            userinfo
        )
        nameList.value = nameList.value

        saveUserInfo(userinfo)
    }

    fun onJobSelected(selectedJob: String) {
        job.value = selectedJob
    }

    fun onTeamSelected(teamName: String) {
        team.value = teamName
    }

    fun onItemLongClick(userInfo: userInfoProto) {
        deleteUserInfo(userInfo)
    }

    fun saveUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            userInfoRepo.addUser(userInfo)
        }
    }

    fun deleteUserInfo(userInfo: userInfoProto, onDeleteDone: () -> Unit = { }) {
        viewModelScope.launch {
            userInfoRepo.removeUser(userInfo)
            onDeleteDone()
        }
    }

    fun editUserInfo(userInfo: userInfoProto) {
        viewModelScope.launch {
            userInfoRepo.editUser(userInfo)
        }
    }
}