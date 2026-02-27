package com.example.app

import androidx.lifecycle.ViewModelProvider
import com.example.app.backend.DispatchedLocationsApi
import com.example.app.presentation.ViewModelFactory
import com.example.app.presentation.chooseshop.ChooseShopViewModel
import com.example.app.presentation.chooseshop.ChooseShopViewModelImpl
import com.example.app.presentation.selectedshop.SelectedShopViewModel
import com.example.app.presentation.selectedshop.SelectedShopViewModelImpl
import com.example.app.presentation.shoplistselection.ShopListSelectionViewModel
import com.example.app.presentation.shoplistselection.ShopListSelectionViewModelImpl
import com.example.app.view.ChooseShopFragment
import com.example.app.view.SelectedShopFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun inject(fragment: ChooseShopFragment) {
    (fragment.context as? Navigator)?.let { fragment.navigator = it }
    fragment.chooseShopViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<ChooseShopViewModel>(ChooseShopViewModelImpl(brandShops))
        ).get(
            ChooseShopViewModelImpl::class.java
        )
    fragment.shopSelectionViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<ShopListSelectionViewModel>(
                ShopListSelectionViewModelImpl(indexSelection)
            )
        ).get(
            ShopListSelectionViewModelImpl::class.java
        )
}

fun inject(fragment: SelectedShopFragment) {
    (fragment.context as? Navigator)?.let { fragment.navigator = it }
    fragment.selectionViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<SelectedShopViewModel>(SelectedShopViewModelImpl(shopSelection))
        ).get(
            SelectedShopViewModelImpl::class.java
        )
}

val gson: Gson by lazy {
    GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .registerTypeAdapter(Location::class.java, LocationDeserializer())
        .create()
}

val okHttpClient by lazy {
    OkHttpClient
        .Builder()
        .withLoggedTraffic()
        .build()
}

val api: LocationsApi by lazy {
    DispatchedLocationsApi(
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .baseUrl("https://api-staging-1.getsquire.com/v1/")
            .build()
            .create(LocationsApi::class.java)
    )
}

val indexSelection by lazy { MutableStateFlow(-1) }

val shopSelection by lazy { MutableStateFlow<Shop?>(null) }

val brandShops by lazy {
    DispatchedBrandShops(
        brandShops = BrandShopsImpl(
            api = api,
            currentSelectionIndex = ToggleIndexSelection(indexSelection),
            currentSelectionShop = ToggleShopSelection(shopSelection)
        )
    )
}