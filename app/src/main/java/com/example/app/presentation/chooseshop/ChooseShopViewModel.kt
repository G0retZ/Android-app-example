package com.example.app.presentation.chooseshop

import androidx.lifecycle.viewModelScope
import com.example.app.BrandShops
import com.example.app.Shop
import com.example.app.presentation.ViewModel
import com.example.app.presentation.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel of shops list view.
 */
interface ChooseShopViewModel : ViewModel<ChooseShopViewActions, String> {
    /**
     * @param index - selected item
     */
    fun selectItem(index: Int)

    fun reloadShops()

    fun close()
}

class ChooseShopViewModelImpl(private val brandShops: BrandShops) :
    androidx.lifecycle.ViewModel(), ChooseShopViewModel {

    override val viewStates =
        MutableStateFlow<ViewState<ChooseShopViewActions>>(ChooseShopViewStatePending)
    override val navigation = MutableSharedFlow<String>()
    private var job: Job = Job().apply { cancel() }

    init {
        reloadShops()
    }

    override fun selectItem(index: Int) {
        if (!job.isActive) {
            job = viewModelScope.launch {
                try {
                    brandShops.selectShopAt(index)
                } catch (error: Throwable) {
                    consumeError(error)
                }
            }
        }
    }

    override fun reloadShops() {
        if (!job.isActive) {
            job = viewModelScope.launch {
                viewStates.emit(ChooseShopViewStatePending)
                try {
                    consumeShops(brandShops.getBrandShops())
                } catch (error: Throwable) {
                    consumeError(error)
                }
            }
        }
    }

    override fun close() {
        viewModelScope.launch {
            navigation.emit(CLOSE_CHOOSE_SHOP)
        }
    }

    private suspend fun consumeShops(shops: List<Shop>) = viewStates.emit(
        ChooseShopViewStateReady(
            shops.map(::ChooseShopListItem)
        )
    )

    private suspend fun consumeError(error: Throwable) = viewStates.emit(
        ChooseShopViewStateError(error.message ?: "Something went wrong")
    )

    override fun onCleared() = super.onCleared()
        .also { job.cancel() }
}
