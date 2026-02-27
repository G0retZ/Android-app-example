package com.example.app.presentation.shoplistselection

import androidx.lifecycle.viewModelScope
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
 * ViewModel of shops list selection.
 */
interface ShopListSelectionViewModel : ViewModel<ShopListSelectionViewActions, String> {
    fun accept()
}

class ShopListSelectionViewModelImpl(currentSelection: Flow<Int>) :
    androidx.lifecycle.ViewModel(), ShopListSelectionViewModel {

    override val viewStates =
        MutableStateFlow<ViewState<ShopListSelectionViewActions>>(ShopListSelectionStateNotSelected)
    override val navigation = MutableSharedFlow<String>()
    private val selectionJob: Job = currentSelection
        .onEach {
            if (it < 0) {
                viewStates.emit(ShopListSelectionStateNotSelected)
            } else {
                viewStates.emit(ShopListSelectionStateSelected(it))
            }
        }
        .launchIn(viewModelScope)

    override fun accept() {
        viewModelScope.launch {
            navigation.emit(TO_SELECTION_DETAILS)
        }
    }

    override fun onCleared() = super.onCleared().also {
        selectionJob.cancel()
    }
}
