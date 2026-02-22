package com.example.app

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

fun OkHttpClient.Builder.withLoggedTraffic() = addNetworkInterceptor(
    HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) = Timber.v(message)
    }).setLevel(HttpLoggingInterceptor.Level.BODY)
)
