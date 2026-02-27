package com.example.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalCoroutinesApi::class)
class BrandShopsTest {

    private lateinit var useCase: BrandShops
    private lateinit var api: LocationsApiMock
    private lateinit var continuation: Continuation<Result<LocationsResponse>>


    @Before
    fun setUp() {
        api = LocationsApiMock { continuation = it }
    }

    /* Check answers on API data request */
    /**
     * Should answer with error.
     */
    @Test
    fun answerIOException() = runTest {
        // Given:
        useCase = BrandShopsImpl(api, { }, { })

        // Action:
        val values = async { runCatching { useCase.getBrandShops() } }
        advanceUntilIdle()
        continuation.resumeWithException(IOException())

        // Effect:
        assertTrue(values.await().exceptionOrNull() is IOException)
    }

    /**
     * Should answer without distortion.
     */
    @Test
    fun answerWithVehiclesList() = runTest {
        // Given:
        useCase = BrandShopsImpl(api, { }, { })

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(
            Result.success(
                LocationsResponse(
                    Brand(
                        shops = listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Effect:
        assertEquals(
            listOf(
                Shop(id = "0"),
                Shop(id = "1"),
                Shop(id = "2"),
                Shop(id = "3"),
                Shop(id = "4")
            ),
            values.await()
        )
    }

    /* Check interaction with choice data observer */
    /**
     * Should clear touch choice data observer on result and error.
     */
    @Test
    fun shouldClearChoiceObserverOnError() = runTest {
        // Given:
        val results = mutableListOf<Int>()
        useCase = BrandShopsImpl(api, results::add, { })

        // Action:
        val values = async { runCatching { useCase.getBrandShops() } }
        advanceUntilIdle()
        continuation.resume(Result.failure(IOException()))
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(-1), results)
        values.await()
    }

    /**
     * Should clear touch choice data observer on result.
     */
    @Test
    fun shouldClearChoiceObserverOnResult() = runTest {
        // Given:
        val results = mutableListOf<Int>()
        useCase = BrandShopsImpl(api, results::add, { })

        // Action:
        val values = async { runCatching { useCase.getBrandShops() } }
        advanceUntilIdle()
        continuation.resume(Result.success(LocationsResponse(Brand(emptyList()))))
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(-1), results)
        values.await()
    }

    /**
     * Should provide the choice.
     */
    @Test
    fun shouldProvideSelectionToChoiceObserver() = runTest {
        // Given:
        val results = mutableListOf<Int>()
        useCase = BrandShopsImpl(api, results::add, { })

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(
            Result.success(
                LocationsResponse(
                    Brand(
                        shops = listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )
        advanceUntilIdle()
        useCase.selectShopAt(3)
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(-1, 3), results)
        values.await()
    }

    /**
     * Should not touch choice observer if selection is wrong.
     */
    @Test
    fun doNotTouchChoiceObserverIfSelectionInvalid() = runTest {
        // Given:
        val results = mutableListOf<Int>()
        useCase = BrandShopsImpl(api, results::add, { })

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(Result.success(LocationsResponse(Brand(emptyList()))))
        advanceUntilIdle()
        runCatching { useCase.selectShopAt(-1) }
        runCatching { useCase.selectShopAt(7) }

        // Effect:
        assertEquals(listOf(-1), results)
        values.await()
    }

    /* Check interaction with selected shop data observer */
    /**
     * Should clear touch choice data observer on error.
     */
    @Test
    fun shouldClearSelectedShopObserverOnError() = runTest {
        // Given:
        val results = mutableListOf<Shop?>()
        useCase = BrandShopsImpl(api, { }, results::add)

        // Action:
        val values = async { runCatching { useCase.getBrandShops() } }
        advanceUntilIdle()
        continuation.resume(Result.failure(IOException()))
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(null), results)
        values.await()
    }

    /**
     * Should clear touch choice data observer on result.
     */
    @Test
    fun shouldClearSelectedShopObserverOnResult() = runTest {
        // Given:
        val results = mutableListOf<Shop?>()
        useCase = BrandShopsImpl(api, { }, results::add)

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(Result.success(LocationsResponse(Brand(emptyList()))))
        advanceUntilIdle()

        // Effect:
        assertEquals(listOf(null), results)
        values.await()
    }

    /**
     * Should provide the choice.
     */
    @Test
    fun shouldProvideSelectionToSelectedShopObserver() = runTest {
        // Given:
        val results = mutableListOf<Shop?>()
        useCase = BrandShopsImpl(api, { }, results::add)

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(
            Result.success(
                LocationsResponse(
                    Brand(
                        shops = listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )
        advanceUntilIdle()
        useCase.selectShopAt(3)

        // Effect:
        assertEquals(listOf(null, Shop(id = "3")), results)
        values.await()
    }

    /**
     * Should not touch choice observer if selection is wrong.
     */
    @Test
    fun doNotTouchSelectedShopObserverIfSelectionInvalid() = runTest {
        // Given:
        val results = mutableListOf<Shop?>()
        useCase = BrandShopsImpl(api, { }, results::add)

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(Result.success(LocationsResponse(Brand(emptyList()))))
        advanceUntilIdle()
        runCatching { useCase.selectShopAt(-1) }
        runCatching { useCase.selectShopAt(7) }

        // Effect:
        assertEquals(listOf(null), results)
        values.await()
    }

    /* Check answers on selection */
    /**
     * Should answer with out of range error.
     */
    @Test
    fun shouldAnswerWithOutOfBoundsError() = runTest {
        // Given:
        useCase = BrandShopsImpl(api, { }, { })

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(Result.success(LocationsResponse(Brand(emptyList()))))
        advanceUntilIdle()
        val test = runCatching { useCase.selectShopAt(-1) }

        // Effect:
        assertTrue(test.exceptionOrNull() is IndexOutOfBoundsException)
        values.await()
    }

    /**
     * Should answer with success.
     */
    @Test
    fun shouldAnswerWithSuccess() = runTest {
        // Given:
        useCase = BrandShopsImpl(api, { }, { })

        // Action:
        val values = async { useCase.getBrandShops() }
        advanceUntilIdle()
        continuation.resume(
            Result.success(
                LocationsResponse(
                    Brand(
                        shops = listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )
        advanceUntilIdle()
        useCase.selectShopAt(1)

        // Effect: no exception
        values.await()
    }
}

class LocationsApiMock(private val capture: Function1<Continuation<Result<LocationsResponse>>, Unit>) :
    LocationsApi {
    override suspend fun loadHome(brandId: String): Result<LocationsResponse> =
        suspendCancellableCoroutine {
            capture(it)
        }
}