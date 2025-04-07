package com.team01.project.global.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RsData<T>(
    val code: String,
    val msg: String,
    val data: T? = null
) {
    @get:JsonIgnore
    val statusCode: Int
        get() = code.split("-")[0].toInt()
}
