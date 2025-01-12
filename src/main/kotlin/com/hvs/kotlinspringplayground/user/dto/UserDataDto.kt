package com.hvs.kotlinspringplayground.user.dto

import java.util.UUID

data class UserDataDto(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val artistIdList: List<String>? = null
)
