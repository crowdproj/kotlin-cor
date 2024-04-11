package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.*
import com.crowdproj.kotlin.cor.base.BaseCorWorkerDsl

@CorDslMarker
fun <T,C> ICorAddExecDsl<T,C>.worker(
    function: CorWorkerDsl<T,C>.() -> Unit
) {
    add(
        CorWorkerDsl<T,C>(this.config).apply(function)
    )
}

@CorDslMarker
fun <T,C> ICorAddExecDsl<T,C>.worker(
    title: String,
    description: String = "",
    function: suspend T.() -> Unit
) {
    add(
        CorWorkerDsl<T,C>(this.config).apply {
            this.title = title
            this.description = description
            this.handle(function)
        }
    )
}

class CorWorker<T>(
    override val title: String,
    override val description: String = "",
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockHandle: suspend T.() -> Unit = {},
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorWorker<T> {
    override suspend fun on(context: T): Boolean = blockOn(context)
    override suspend fun handle(context: T) = blockHandle(context)
    override suspend fun except(context: T, e: Throwable) = blockExcept(context, e)
}

/**
 * DLS context of a single execution. Cannot be expanded by other chains.
 */
@CorDslMarker
class CorWorkerDsl<T,C>(config: C) : BaseCorWorkerDsl<T,C>(config) {

    override fun build(): ICorExec<T> = CorWorker<T>(
        title = title,
        description = description,
        blockOn = blockOn,
        blockHandle = blockHandle,
        blockExcept = blockExcept
    )

    override fun on(function: suspend T.() -> Boolean) {
        blockOn = function
    }

    override fun handle(function: suspend T.() -> Unit) {
        blockHandle = function
    }

    override fun except(function: suspend T.(e: Throwable) -> Unit) {
        blockExcept = function
    }

}
