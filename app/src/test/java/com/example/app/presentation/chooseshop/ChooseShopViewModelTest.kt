package com.example.app.presentation.chooseshop

import com.example.app.BrandShops
import com.example.app.MainCoroutineRule
import com.example.app.Shop
import com.example.app.presentation.ViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
class ChooseShopViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ChooseShopViewModel
    private lateinit var brandShops: BrandShopsMock
    private lateinit var continuation: Continuation<List<Shop>>
    private lateinit var selectionContinuation: Continuation<Unit>

    @Before
    fun setUp() {
        brandShops = BrandShopsMock(
            capture = { continuation = it },
            selectionCapture = { selectionContinuation = it }
        )
    }

    /* Check interactions работу with use case. */
    @Suppress("UnusedFlow")
    @Test
    fun shouldNotTouchUseCaseOnSubscriptions() = runTest {
        // Given:
        viewModel = ChooseShopViewModelImpl(brandShops)

        // Action:
        advanceUntilIdle()
        continuation.resume(
            listOf(
                Shop(id = "1"),
                Shop(id = "2"),
                Shop(id = "3"),
                Shop(id = "4")
            )
        )
        advanceUntilIdle()
        viewModel.navigation
        viewModel.viewStates
        viewModel.navigation
        viewModel.viewStates

        // Effect:
        assertEquals(1, brandShops.brandShopsCallCount)
        assertEquals(emptyList<Int>(), brandShops.selectShopCallArgs)
    }

    @Test
    fun shouldAskUseCaseToSelectByIndex() = runTest {
        // Given:
        viewModel = ChooseShopViewModelImpl(brandShops)

        // Action:
        advanceUntilIdle()
        continuation.resume(emptyList())
        advanceUntilIdle()
        viewModel.selectItem(-1)
        advanceUntilIdle()
        selectionContinuation.resume(Unit)
        advanceUntilIdle()
        viewModel.selectItem(2)
        advanceUntilIdle()
        selectionContinuation.resume(Unit)
        advanceUntilIdle()
        viewModel.selectItem(3)
        advanceUntilIdle()
        selectionContinuation.resume(Unit)
        advanceUntilIdle()

        // Effect:
        assertEquals(1, brandShops.brandShopsCallCount)
        assertEquals(listOf(-1, 2, 3), brandShops.selectShopCallArgs)
    }

    @Test
    fun shouldNotTouchUseCaseDuringReload() = runTest {
        // Given:
        viewModel = ChooseShopViewModelImpl(brandShops)

        // Action:
        advanceUntilIdle()
        viewModel.selectItem(-1)
        advanceUntilIdle()
        viewModel.selectItem(2)
        advanceUntilIdle()
        viewModel.selectItem(3)
        advanceUntilIdle()
        continuation.resume(emptyList())
        advanceUntilIdle()

        // Effect:
        assertEquals(1, brandShops.brandShopsCallCount)
        assertEquals(emptyList<Int>(), brandShops.selectShopCallArgs)
    }

    @Test
    fun shouldNotTouchUseCaseDuringSelection() = runTest {
        // Given:
        viewModel = ChooseShopViewModelImpl(brandShops)

        // Action:
        advanceUntilIdle()
        continuation.resume(emptyList())
        advanceUntilIdle()
        viewModel.selectItem(-1)
        advanceUntilIdle()
        viewModel.selectItem(2)
        advanceUntilIdle()
        viewModel.selectItem(3)
        advanceUntilIdle()
        selectionContinuation.resume(Unit)
        advanceUntilIdle()

        // Effect:
        assertEquals(1, brandShops.brandShopsCallCount)
        assertEquals(listOf(-1), brandShops.selectShopCallArgs)
    }

    /* Check view state switching. */
    @Test
    fun shouldSetPendingViewStateToViewStatesInitially() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)

        // Action:
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(ChooseShopViewStatePending), result)
        job.cancel()
    }

    @Test
    fun shouldSetErrorViewStateToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        continuation.resumeWithException(IOException("Error"))
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ChooseShopViewStatePending,
                ChooseShopViewStateError("Error")
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldSetReadyViewStateWitchCorrectListToViewStates() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        continuation.resume(
            listOf(
                Shop(id = "1"),
                Shop(id = "2"),
                Shop(id = "3"),
                Shop(id = "4")
            )
        )
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ChooseShopViewStatePending,
                ChooseShopViewStateReady(
                    listOf(
                        ChooseShopListItem(Shop(id = "1")),
                        ChooseShopListItem(Shop(id = "2")),
                        ChooseShopListItem(Shop(id = "3")),
                        ChooseShopListItem(Shop(id = "4"))
                    )
                )
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldSetPendingViewStateToViewStatesOnRetry() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        continuation.resumeWithException(IOException("Error"))
        advanceUntilIdle()
        viewModel.reloadShops()
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ChooseShopViewStatePending,
                ChooseShopViewStateError("Error"),
                ChooseShopViewStatePending
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldSetErrorViewStateToViewStatesOnSelectionFailure() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        continuation.resume(emptyList())
        advanceUntilIdle()
        viewModel.selectItem(-1)
        advanceUntilIdle()
        selectionContinuation.resumeWithException(IndexOutOfBoundsException("Error 2"))
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ChooseShopViewStatePending,
                ChooseShopViewStateReady(listOf()),
                ChooseShopViewStateError("Error 2")
            ),
            result
        )
        job.cancel()
    }

    @Test
    fun shouldNotSetOtherViewStateToViewStatesOnSelectionSuccess() = runTest {
        // Given:
        val result = mutableListOf<ViewState<ChooseShopViewActions>>()
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.viewStates.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        continuation.resume(emptyList())
        advanceUntilIdle()
        viewModel.selectItem(-1)
        advanceUntilIdle()
        selectionContinuation.resume(Unit)
        advanceUntilIdle()

        // Effect:
        assertEquals(
            listOf(
                ChooseShopViewStatePending,
                ChooseShopViewStateReady(listOf())
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
        viewModel = ChooseShopViewModelImpl(brandShops)
        val job = viewModel.navigation.onEach(result::add).launchIn(this)

        // Action:
        advanceUntilIdle()
        viewModel.close()
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(CLOSE_CHOOSE_SHOP), result)
        job.cancel()
    }
}

class BrandShopsMock(
    private val capture: Function1<Continuation<List<Shop>>, Unit>,
    private val selectionCapture: Function1<Continuation<Unit>, Unit>
) : BrandShops {
    var brandShopsCallCount = 0
        private set
    var selectShopCallArgs = mutableListOf<Int>()
        private set

    override suspend fun getBrandShops(): List<Shop> = suspendCancellableCoroutine {
        brandShopsCallCount++
        capture(it)
    }

    override suspend fun selectShopAt(selection: Int) = suspendCancellableCoroutine {
        selectShopCallArgs.add(selection)
        selectionCapture(it)
    }
}