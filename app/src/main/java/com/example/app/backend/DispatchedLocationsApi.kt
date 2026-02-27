package com.example.app.backend

import com.example.app.LocationsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DispatchedLocationsApi(
    private val api: LocationsApi
) : LocationsApi {
    override suspend fun loadHome(brandId: String) =
        withContext(Dispatchers.IO) { api.loadHome(brandId) }
}