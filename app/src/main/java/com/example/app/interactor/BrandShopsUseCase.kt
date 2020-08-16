package com.example.app.interactor

import com.example.app.LocationsApi
import com.example.app.Shop
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.Single

interface BrandShopsUseCase {
    val brandShops: Single<List<Shop>>
    fun selectShopAt(shop: Shop?): Completable
}

class BrandShopsUseCaseImpl(
    api: LocationsApi,
    private val choice: Observer<Shop>
) : BrandShopsUseCase {

    override val brandShops: Single<List<Shop>> = TODO("Not yet implemented")

    override fun selectShopAt(shop: Shop?) = TODO("Not yet implemented")
}

class ShopChoiceSharer() : MemoryDataSharer<Shop>()
