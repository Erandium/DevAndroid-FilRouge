package com.jdock.fil_rouge.authentication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("token")
    val token : String,
    @SerialName("expire")
    val expire : String
):java.io.Serializable
