package com.multiplatform.data.model.mapper

interface BaseMapper<in T, out U> {
    fun mapTo(param: T?): U
}