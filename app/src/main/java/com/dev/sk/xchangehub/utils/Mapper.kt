package com.dev.sk.xchangehub.utils

interface Mapper<U, V> {
    fun mapTo(dataModel: U): V
}