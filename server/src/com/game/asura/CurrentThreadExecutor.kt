package com.game.asura

import java.util.concurrent.Executor

class CurrentThreadExecutor : Executor {
    override fun execute(r: Runnable) {
        r.run()
    }
}