package com.example.app.presentation.chooseshop

import androidx.lifecycle.MutableLiveData
import com.example.app.Shop
import com.example.app.interactor.BrandShopsUseCase
import com.example.app.presentation.SingleLiveEvent
import com.example.app.presentation.ViewModel
import com.example.app.presentation.ViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

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

class ChooseShopViewModelImpl(private val brandShopsUseCase: BrandShopsUseCase) :
    androidx.lifecycle.ViewModel(), ChooseShopViewModel {

    override val viewStateLiveData = MutableLiveData<ViewState<ChooseShopViewActions>>()
    override val navigationLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    private var shopsDisposable: Disposable = Disposables.disposed()
    private var choiceDisposable: Disposable = Disposables.disposed()

    init {
        reloadShops()
    }

    override fun selectItem(index: Int) {
        if (choiceDisposable.isDisposed) {
            choiceDisposable = brandShopsUseCase.selectShopAt(index)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, this::consumeError)
        }
    }

    override fun reloadShops() {
        if (shopsDisposable.isDisposed) {
            viewStateLiveData.postValue(ChooseShopViewStatePending())
            shopsDisposable = brandShopsUseCase.brandShops
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::consumeShops, this::consumeError)
        }
    }

    override fun close() {
        navigationLiveData.postValue(CLOSE_CHOOSE_SHOP)
    }

    private fun consumeShops(vehicles: List<Shop>) = viewStateLiveData.postValue(
        ChooseShopViewStateReady(
            vehicles.map(::ChooseShopListItem)
        )
    )

    private fun consumeError(error: Throwable) = viewStateLiveData.postValue(
        ChooseShopViewStateError(error.message ?: "Something went wrong")
    )

    override fun onCleared() = super.onCleared().also {
        shopsDisposable.dispose()
        choiceDisposable.dispose()
    }
}
