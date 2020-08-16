package com.example.app

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Test Rule, that sets Trampoline schedulers for UseCases.
 */
class UseCaseThreadTestRule : TestRule {
    override fun apply(base: Statement, description: Description) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            RxJavaPlugins.reset()
            RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
            base.evaluate()
            RxJavaPlugins.reset()
        }
    }
}