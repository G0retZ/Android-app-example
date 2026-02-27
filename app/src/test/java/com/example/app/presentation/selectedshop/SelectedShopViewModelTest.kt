package com.example.app.presentation.selectedshop

import com.example.app.MainCoroutineRule
import com.example.app.Shop
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
class SelectedShopViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var selectionFlow: MutableSharedFlow<Shop?>

    @Before
    fun setUp() {
        selectionFlow = MutableSharedFlow()
    }

    /* Check view state switching. */
    @Test
    fun shouldSetNotSelectedViewStateToViewStatesInitially() = runTest {
        // Given:
        val result = mutableListOf<ViewState<SelectedShopViewActions>>()
        val viewModel = SelectedShopViewModelImpl(selectionFlow)

        // Action:
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(SelectedShopStateNotSelected), result)
        job.cancel()
    }

    @Test
    fun shouldSetSelectedViewStateToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<SelectedShopViewActions>>()
        val viewModel = SelectedShopViewModelImpl(selectionFlow)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        selectionFlow.emit(Shop(id = "2"))
        advanceUntilIdle()
        selectionFlow.emit(Shop(id = "5"))
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                SelectedShopStateNotSelected,
                SelectedShopStateSelected(Shop(id = "2")),
                SelectedShopStateSelected(Shop(id = "5"))
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldSetNotSelectedViewStateToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<SelectedShopViewActions>>()
        val viewModel = SelectedShopViewModelImpl(selectionFlow)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        selectionFlow.emit(Shop(id = "2"))
        advanceUntilIdle()
        selectionFlow.emit(null)
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                SelectedShopStateNotSelected,
                SelectedShopStateSelected(Shop(id = "2")),
                SelectedShopStateNotSelected,
            ),
            result
        )
        job.cancel()
    }

    /* Check navigation */
    @Test
    fun setNavigateToCloseSelectionDetailsOnClose() = runTest {
        // Given:
        val result = mutableListOf<String>()
        val viewModel = SelectedShopViewModelImpl(selectionFlow)
        val job = viewModel.navigation.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        viewModel.close()
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(CLOSE_SELECTION_DETAILS),
            result
        )
        job.cancel()
    }

    @Test
    fun setNavigateToCloseSelectionDetailsOnComplete() = runTest {
        // Given:
        val result = mutableListOf<String>()
        val viewModel = SelectedShopViewModelImpl(selectionFlow)
        val job = viewModel.navigation.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        selectionFlow.emit(null)
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(CLOSE_SELECTION_DETAILS),
            result
        )
        job.cancel()
    }
}
