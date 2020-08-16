package com.example.app.interactor

import org.junit.Test

class ShopChoiceSharerTest {

    /**
     * Should pass selection.
     */
    @Test
    fun shouldPassSelection() {
        // Given:
        val sharer = ShopChoiceSharer()
        val test = sharer.get().test()

        // Action:
        sharer.onNext(-1)
        sharer.onNext(0)
        sharer.onNext(-1)
        sharer.onNext(2)
        sharer.onNext(3)
        sharer.onNext(0)

        // Effect:
        test.assertValues(-1, 0, -1, 2, 3, 0)
    }


    /**
     * Should deselect (set to -1) on selection repeat.
     */
    @Test
    fun shouldDeselectOnRepeat() {
        // Given:
        val sharer = ShopChoiceSharer()
        val test = sharer.get().test()

        // Action:
        sharer.onNext(-1)
        sharer.onNext(-1)
        sharer.onNext(0)
        sharer.onNext(0)
        sharer.onNext(1)
        sharer.onNext(1)
        sharer.onNext(2)
        sharer.onNext(2)

        // Effect:
        test.assertValues(-1, -1, 0, -1, 1, -1, 2, -1)
    }
}
