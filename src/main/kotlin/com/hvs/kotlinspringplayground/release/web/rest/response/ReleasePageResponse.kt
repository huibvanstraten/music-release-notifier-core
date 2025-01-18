package com.hvs.kotlinspringplayground.release.web.rest.response

import com.hvs.kotlinspringplayground.release.domain.jpa.Release

data class ReleasePagedResponse(
    val content: List<Release>,
    val total: Long,
    val offset: Int,
    val limit: Int,
    val next: String?,
    val previous: String?
)
