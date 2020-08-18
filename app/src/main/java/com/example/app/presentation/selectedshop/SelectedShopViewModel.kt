package com.example.app.presentation.selectedshop

import androidx.lifecycle.MutableLiveData
import com.example.app.Shop
import com.example.app.interactor.DataReceiver
import com.example.app.presentation.SingleLiveEvent
import com.example.app.presentation.ViewModel
import com.example.app.presentation.ViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

/**
 * ViewModel of selected shop.
 */
interface SelectedShopViewModel : ViewModel<SelectedShopViewActions, String> {
    fun accept()
    fun close()
}

class SelectedShopViewModelImpl(private val selectedShopUseCase: DataReceiver<Shop>) :
    androidx.lifecycle.ViewModel(), SelectedShopViewModel {

    override val viewStateLiveData = MutableLiveData<ViewState<SelectedShopViewActions>>()
    override val navigationLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    private var selectionDisposable: Disposable = Disposables.disposed()

    init {
        connect()
    }

    private fun connect() {
        if (selectionDisposable.isDisposed) {
            viewStateLiveData.postValue(SelectedShopStateNotSelected())
            selectionDisposable = selectedShopUseCase.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::consumeSelection, {}) {
                    connect()
                    navigationLiveData.postValue(CLOSE_SELECTION_DETAILS)
                }
        }
    }

    override fun accept() {
    }

    override fun close() {
        navigationLiveData.postValue(CLOSE_SELECTION_DETAILS)
    }

    private fun consumeSelection(shop: Shop) = viewStateLiveData.postValue(
        SelectedShopStateSelected(shop)
    )

    override fun onCleared() = super.onCleared().also {
        selectionDisposable.dispose()
    }
}
