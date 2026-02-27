package com.example.app.presentation.selectedshop

import androidx.lifecycle.viewModelScope
import com.example.app.Shop
import com.example.app.presentation.ViewModel
import com.example.app.presentation.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel of selected shop.
 */
interface SelectedShopViewModel : ViewModel<SelectedShopViewActions, String> {
    fun accept()
    fun close()
}

class SelectedShopViewModelImpl(currentSelection: Flow<Shop?>) :
    androidx.lifecycle.ViewModel(), SelectedShopViewModel {

    override val viewStates =
        MutableStateFlow<ViewState<SelectedShopViewActions>>(SelectedShopStateNotSelected)
    override val navigation = MutableSharedFlow<String>()
    private var selectionJob: Job = currentSelection
        .onEach {
            if (it == null) {
                viewStates.emit(SelectedShopStateNotSelected)
                navigation.emit(CLOSE_SELECTION_DETAILS)
            } else {
                viewStates.emit(SelectedShopStateSelected(it))
            }
        }
        .launchIn(viewModelScope)

    override fun accept() {
    }

    override fun close() {
        viewModelScope.launch {
            navigation.emit(CLOSE_SELECTION_DETAILS)
        }
    }

    override fun onCleared() = super.onCleared().also {
        selectionJob.cancel()
    }
}
