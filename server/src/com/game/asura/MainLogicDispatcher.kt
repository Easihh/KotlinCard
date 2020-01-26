package com.game.asura

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

class MainLogicDispatcher(private val executor: Executor) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        executor.execute(block)
    }
}