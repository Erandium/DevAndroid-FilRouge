package com.jdock.fil_rouge.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdock.fil_rouge.network.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


class UserInfoViewModel : ViewModel() {
    private val userInfoRepository = UserInfoRepository()
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    fun loadUser() {
        viewModelScope.launch {
            _userInfo.value = userInfoRepository.loadUser()
        }
    }

    fun updateUser(user: UserInfo) {
        viewModelScope.launch {
            _userInfo.value = userInfoRepository.updateUser(user)
        }
    }

    fun updateAvatar(avatar : MultipartBody.Part) {
        viewModelScope.launch {
            _userInfo.value = userInfoRepository.updateAvatar(avatar)
        }
    }
}