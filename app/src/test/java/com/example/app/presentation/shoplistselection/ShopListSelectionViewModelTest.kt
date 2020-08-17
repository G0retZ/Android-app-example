package com.example.app.presentation.shoplistselection

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.app.ViewModelThreadTestRule
import com.example.app.interactor.DataReceiver
import com.example.app.presentation.ViewState
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ShopListSelectionViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ShopListSelectionViewModel

    @Mock
    private lateinit var useCase: DataReceiver<Int>

    @Mock
    private lateinit var viewStateObserver: Observer<ViewState<ShopListSelectionViewActions>>

    @Mock
    private lateinit var navigateObserver: Observer<String>

    private lateinit var selectionSubject: PublishSubject<Int>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        selectionSubject = PublishSubject.create<Int>()
        Mockito.`when`(useCase.get()).thenReturn(selectionSubject)
        viewModel = ShopListSelectionViewModelImpl(useCase)
    }

    /* Check interactions with use case. */
    @Test
    fun shouldAskUseCaseForDataInitially() {
        // Effect:
        verify(useCase, Mockito.only()).get()
    }

    @Test
    fun shouldNotTouchUseCaseOnSubscriptions() {
        // Action:
        selectionSubject.onNext(2)
        selectionSubject.onNext(3)
        viewModel.navigationLiveData
        viewModel.viewStateLiveData
        viewModel.navigationLiveData
        viewModel.viewStateLiveData

        // Effect:
        verify(useCase, Mockito.only()).get()
    }

    /* Check view state switching. */
    @Test
    fun shouldSetNotSelectedViewStateToLiveDataInitially() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)

        // Action:
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ShopListSelectionStateNotSelected::class.java)
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetSelectedViewStateToLiveData() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        selectionSubject.onNext(2)
        selectionSubject.onNext(5)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ShopListSelectionStateNotSelected::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ShopListSelectionStateSelected(2)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ShopListSelectionStateSelected(5)
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetNotSelectedViewStateToLiveData() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        selectionSubject.onNext(2)
        selectionSubject.onNext(-1)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ShopListSelectionStateNotSelected::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ShopListSelectionStateSelected(2)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ShopListSelectionStateNotSelected::class.java)
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
    }

    /* Check navigation */
    @Test
    fun setNavigateToSelectionDetailsOnAccept() {
        // Given:
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Action:
        viewModel.accept()

        // Effect:
        Mockito.verify<Observer<String>>(
            navigateObserver,
            Mockito.only()
        ).onChanged(TO_SELECTION_DETAILS)
    }

    companion object {
        @ClassRule
        @JvmField
        val classRule: ViewModelThreadTestRule = ViewModelThreadTestRule()
    }
}