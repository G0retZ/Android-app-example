package com.example.app.interactor

import com.example.app.LocationsApi
import com.example.app.Shop
import io.reactivex.Completable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

interface BrandShopsUseCase {
    val brandShops: Single<List<Shop>>
    fun selectShopAt(selection: Int?): Completable
}

class BrandShopsUseCaseImpl(
    api: LocationsApi,
    private val choice: Observer<Int>,
    private val selectedShop: Observer<Shop>
) : BrandShopsUseCase {

    private var shops: List<Shop> = listOf()

    override val brandShops: Single<List<Shop>> by lazy {
        api.loadHome()
            .subscribeOn(Schedulers.io())
            .map { it.brand?.shops ?: listOf() }
            .observeOn(Schedulers.single())
            .doOnSuccess { shops = it }
            .doOnError { shops = listOf() }
            .doAfterTerminate {
                choice.onNext(-1)
                selectedShop.onComplete()
            }
            .cache()
    }

    override fun selectShopAt(selection: Int?) = Completable.fromCallable {
        when {
            selection == null -> {
                choice.onNext(-1)
                selectedShop.onComplete()
            }
            shops.indices.contains(selection) -> {
                choice.onNext(selection)
                selectedShop.onNext(shops[selection])
            }
            else -> throw IndexOutOfBoundsException("There is no such index: $selection")
        }
    }
}

class ShopChoiceSharer() : MemoryDataSharer<Int>() {
    override fun onNext(data: Int) = super.onNext(if (data == subject.value) -1 else data)
}

class SelectedShopSharer : MemoryDataSharer<Shop>()
