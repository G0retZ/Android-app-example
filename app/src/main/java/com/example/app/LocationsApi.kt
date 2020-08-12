package com.example.app

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface LocationsApi {
    @GET("home/{brandId}")
    fun loadHome(@Path("brandId") brandId: String = "0940f8d3-3fbb-432a-8f44-5a467d1da20f"): Observable<LocationsResponse>
}