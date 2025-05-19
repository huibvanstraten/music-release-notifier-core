package com.hvs.kotlinspringplayground.user.dto

import com.fasterxml.jackson.annotation.JsonFilter

@JsonFilter("userFilter")
data class UserArtistListDto(
    val userId: String,
    val username: String,
    val artistIdList: List<String>
)
