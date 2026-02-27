package com.example.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withContext

interface BrandShops {
    suspend fun getBrandShops(): List<Shop>
    suspend fun selectShopAt(selection: Int)
}

class DispatchedBrandShops(private val brandShops: BrandShops) : BrandShops {
    override suspend fun getBrandShops() = withContext(Dispatchers.Default) {
        brandShops.getBrandShops()
    }

    override suspend fun selectShopAt(selection: Int) = withContext(Dispatchers.Default) {
        brandShops.selectShopAt(selection)
    }
}

class BrandShopsImpl(
    private val api: LocationsApi,
    private val currentSelectionIndex: FlowCollector<Int>,
    private val currentSelectionShop: FlowCollector<Shop?>
) : BrandShops {
    private var shops: List<Shop> = listOf()

    override suspend fun getBrandShops() = api.loadHome()
        .map {
            shops = it.brand?.shops ?: listOf()
            shops
        }
        .onSuccess {
            currentSelectionIndex.emit(-1)
            currentSelectionShop.emit(null)
        }
        .onFailure {
            currentSelectionIndex.emit(-1)
            currentSelectionShop.emit(null)
        }
        .getOrThrow()

    override suspend fun selectShopAt(selection: Int) {
        val data = shops[selection]
        currentSelectionIndex.emit(selection)
        currentSelectionShop.emit(data)
    }
}
