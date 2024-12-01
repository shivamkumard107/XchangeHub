package com.dev.sk.xchangehub.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.SOURCE)
annotation class BaseUrl


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey