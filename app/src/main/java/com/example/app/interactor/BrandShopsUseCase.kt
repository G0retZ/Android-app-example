package com.example.app.interactor

import com.example.app.LocationsApi
import com.example.app.Shop
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface BrandShopsUseCase {
    val brandShops: Single<List<Shop>>
    fun selectShopAt(shop: Shop?): Completable
}

class BrandShopsUseCaseImpl(api: LocationsApi, private val choice: Observer<Shop>) :
    BrandShopsUseCase {

    private var shops: List<Shop> = listOf()

    override val brandShops: Single<List<Shop>> by lazy {
        api.loadHome()
            .subscribeOn(Schedulers.io())
            .map { it.brand?.shops ?: listOf() }
            .observeOn(Schedulers.single())
            .doOnSuccess { shops = it }
            .doOnError { shops = listOf() }
            .doAfterTerminate { choice.onComplete() }
            .cache()
    }

    override fun selectShopAt(shop: Shop?) = Completable.fromCallable {
        shop
            ?.let {
                if (shops.contains(it)) {
                    choice.onNext(it)
                } else {
                    throw IndexOutOfBoundsException("There is no such selection: $shop")
                }
            }
            ?: choice.onComplete()
    }
}

class ShopChoiceSharer() : MemoryDataSharer<Shop>()
