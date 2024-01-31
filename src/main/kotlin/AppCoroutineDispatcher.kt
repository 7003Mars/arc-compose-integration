package me.mars

import arc.Events
import arc.graphics.gl.FrameBuffer
import arc.util.Log
import arc.util.TaskQueue
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class)
class AppCoroutineDispatcher(): CoroutineDispatcher(), Delay {
    var started: Boolean = false

    val tasks: TaskQueue = TaskQueue()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        tasks.post(block)
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        TODO("Not yet implemented")
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
        TODO()
    }

    fun runAll() {
        if (!started) return

//        if (!SharedBoolState.bound) {
//        }
        if (tasks.size() != 0) {
            Log.info("Running ${tasks.size()} tasks")
            tasks.run()
        }

//        if (SharedBoolState.bound) {
//        }

    }
}
