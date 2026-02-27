package com.example.app

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun OkHttpClient.Builder.withLoggedTraffic() = addNetworkInterceptor(
    HttpLoggingInterceptor { message -> Log.v("Http", message) }
        .setLevel(HttpLoggingInterceptor.Level.BODY)
)
