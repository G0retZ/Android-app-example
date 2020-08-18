package com.example.app.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator

class HideAnimator(private val yDistance: Float, private val views: List<View>) {

    private var currentAnimator: Animator? = null

    private var visible = true

    fun switchVisibility(visible: Boolean, onFinished: () -> Unit = {}) {
        currentAnimator?.cancel()
        if (this.visible != visible) {
            if (this.visible) {
                createAnimator(views, 0f, yDistance, AccelerateInterpolator())
            } else {
                createAnimator(views, 1f, 0f, DecelerateInterpolator())
            }.animate {
                this.visible = visible
                onFinished.invoke()
            }
        } else {
            onFinished.invoke()
        }
    }

    private fun AnimatorSet.animate(onResult: () -> Unit) {
        addListener(
            object : AnimatorListenerAdapter() {
                private var cancelled = false

                override fun onAnimationStart(animation: Animator) {
                    currentAnimator = animation
                    cancelled = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    cancelled = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    currentAnimator = null
                    if (!cancelled) onResult.invoke()
                }
            })
        start()
    }

    private fun createAnimator(
        views: List<View>,
        alpha: Float,
        yDistance: Float,
        interpol: Interpolator
    ): AnimatorSet {
        return AnimatorSet()
            .apply {
                playTogether(views
                    .map {
                        listOf(
                            ObjectAnimator.ofFloat(it, View.ALPHA, alpha),
                            ObjectAnimator.ofFloat(it, View.TRANSLATION_Y, yDistance)
                        )
                    }
                    .flatten()
                )
                duration = 200
                interpolator = interpol
            }
    }
}