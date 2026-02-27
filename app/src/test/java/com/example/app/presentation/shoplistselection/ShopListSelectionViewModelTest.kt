package com.example.app.presentation.shoplistselection

import com.example.app.MainCoroutineRule
import com.example.app.presentation.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShopListSelectionViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var selectionFlow: MutableSharedFlow<Int>

    @Before
    fun setUp() {
        selectionFlow = MutableSharedFlow()
    }

    /* Check view state switching. */
    @Test
    fun shouldSetNotSelectedViewStateToViewStatesInitially() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ShopListSelectionViewActions>>()
        val viewModel = ShopListSelectionViewModelImpl(selectionFlow)

        // Action:
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(ShopListSelectionStateNotSelected), result)
        job.cancel()
    }

    @Test
    fun shouldSetSelectedViewStateToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ShopListSelectionViewActions>>()
        val viewModel = ShopListSelectionViewModelImpl(selectionFlow)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        selectionFlow.emit(2)
        advanceUntilIdle()
        selectionFlow.emit(5)
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ShopListSelectionStateNotSelected,
                ShopListSelectionStateSelected(2),
                ShopListSelectionStateSelected(5)
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldSetNotSelectedViewStateToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ShopListSelectionViewActions>>()
        val viewModel = ShopListSelectionViewModelImpl(selectionFlow)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        selectionFlow.emit(2)
        advanceUntilIdle()
        selectionFlow.emit(-1)
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ShopListSelectionStateNotSelected,
                ShopListSelectionStateSelected(2),
                ShopListSelectionStateNotSelected
            ),
            result
        )
        job.cancel()
    }

    /* Check navigation */
    @Test
    fun setNavigateToSelectionDetailsOnAccept() = runTest {
        // Given:
        val result = mutableListOf<String>()
        val viewModel = ShopListSelectionViewModelImpl(selectionFlow)
        val job = viewModel.navigation.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        viewModel.accept()
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(TO_SELECTION_DETAILS), result)
        job.cancel()
    }
}
