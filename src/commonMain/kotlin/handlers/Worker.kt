package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.ICorChainDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.ICorWorker
import com.crowdproj.kotlin.cor.ICorWorkerDsl
import com.crowdproj.kotlin.cor.CorDslMarker

@CorDslMarker
fun <T> ICorChainDsl<T>.worker(
    function: CorWorkerDsl<T>.() -> Unit
) {
    add(
        CorWorkerDsl<T>().apply(function)
    )
}

@CorDslMarker
fun <T> ICorChainDsl<T>.worker(
    title: String,
    description: String = "",
    function: suspend T.() -> Unit
) {
    add(
        CorWorkerDsl<T>(
            title = title,
            description = description,
            blockHandle = function
        )
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

@CorDslMarker
class CorWorkerDsl<T>(
    override var title: String = "",
    override var description: String = "",
    private var blockOn: suspend T.() -> Boolean = { true },
    private var blockHandle: suspend T.() -> Unit = {},
    private var blockExcept: suspend T.(e: Throwable) -> Unit = { e: Throwable -> throw e },
) : ICorWorkerDsl<T> {

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
