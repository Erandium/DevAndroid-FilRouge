package com.jdock.fil_rouge.user

import com.jdock.fil_rouge.network.Api
import com.jdock.fil_rouge.network.UserInfo
import okhttp3.MultipartBody

class UserInfoRepository {
    private val userWebService = Api.userWebService

    suspend fun loadUser(): UserInfo? {
        val response = userWebService.getInfo()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateUser(user: UserInfo) : UserInfo? {
        val response = userWebService.update(user)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateAvatar(avatar: MultipartBody.Part) : UserInfo? {
        val response = userWebService.updateAvatar(avatar)
        return if (response.isSuccessful) response.body() else null
    }


}