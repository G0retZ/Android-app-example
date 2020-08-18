package com.example.app

import androidx.lifecycle.ViewModelProvider
import com.example.app.interactor.BrandShopsUseCaseImpl
import com.example.app.interactor.SelectedShopSharer
import com.example.app.interactor.ShopChoiceSharer
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

fun inject(fragment: ChooseShopFragment) {
    fragment.chooseShopViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<ChooseShopViewModel>(ChooseShopViewModelImpl(brandShopsUseCase))
        ).get(
            ChooseShopViewModelImpl::class.java
        )
    fragment.shopSelectionViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<ShopListSelectionViewModel>(
                ShopListSelectionViewModelImpl(selectionSharer)
            )
        ).get(
            ShopListSelectionViewModelImpl::class.java
        )
}

fun inject(fragment: SelectedShopFragment) {
    fragment.selectionViewModel =
        ViewModelProvider(
            fragment,
            ViewModelFactory<SelectedShopViewModel>(SelectedShopViewModelImpl(selectedShopSharer))
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
        .apply {
            if (BuildConfig.DEBUG) {
                addNetworkInterceptor(
                    HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            Timber.v(message)
                        }
                    }).setLevel(HttpLoggingInterceptor.Level.BODY)
                )
            }
        }
        .build()
}

val api: LocationsApi by lazy {

    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .build()
        .create(LocationsApi::class.java)
}

val selectionSharer by lazy(::ShopChoiceSharer)

val selectedShopSharer by lazy(::SelectedShopSharer)

val brandShopsUseCase by lazy {
    BrandShopsUseCaseImpl(api, selectionSharer, selectedShopSharer)
}