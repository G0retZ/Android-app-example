package com.example.app.presentation.shoplistselection

import androidx.lifecycle.MutableLiveData
import com.example.app.interactor.DataReceiver
import com.example.app.presentation.SingleLiveEvent
import com.example.app.presentation.ViewModel
import com.example.app.presentation.ViewState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

/**
 * ViewModel of shops list selection.
 */
interface ShopListSelectionViewModel : ViewModel<ShopListSelectionViewActions, String> {
    fun accept()
}

class ShopListSelectionViewModelImpl(selectionUseCase: DataReceiver<Int>) :
    androidx.lifecycle.ViewModel(), ShopListSelectionViewModel {

    override val viewStateLiveData = MutableLiveData<ViewState<ShopListSelectionViewActions>>()
    override val navigationLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    private var selectionDisposable: Disposable = Disposables.disposed()

    init {
        if (selectionDisposable.isDisposed) {
            viewStateLiveData.postValue(ShopListSelectionStateNotSelected())
            selectionDisposable = selectionUseCase.get()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::consumeSelection) {}
        }
    }

    override fun accept() {
        navigationLiveData.postValue(TO_SELECTION_DETAILS)
    }

    private fun consumeSelection(selection: Int) = viewStateLiveData.postValue(
        if (selection < 0) {
            ShopListSelectionStateNotSelected()
        } else {
            ShopListSelectionStateSelected(selection)
        }
    )

    override fun onCleared() = super.onCleared().also {
        selectionDisposable.dispose()
    }
}
