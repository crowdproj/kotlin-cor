package com.crowdproj.kotlin.cor.handlers

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import com.crowdproj.kotlin.cor.*
import com.crowdproj.kotlin.cor.CorDslMarker

@CorDslMarker
fun <T> ICorChainDsl<T>.parallel(function: CorParallelDsl<T>.() -> Unit) {
    add(CorParallelDsl<T>().apply(function))
}

class CorParallel<T>(
    private val execs: List<ICorExec<T>>,
    override val title: String,
    override val description: String = "",
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorWorker<T> {

    override suspend fun on(context: T): Boolean = blockOn(context)
    override suspend fun except(context: T, e: Throwable) = blockExcept(context, e)

    override suspend fun handle(context: T): Unit = coroutineScope {
        execs
            .map { launch { it.exec(context) } }
            .toList()
            .forEach { it.join() }
    }
}

@CorDslMarker
class CorParallelDsl<T>(
    override var title: String = "",
    override var description: String = "",
    private val workers: MutableList<ICorExecDsl<T>> = mutableListOf(),
    private var blockOn: suspend T.() -> Boolean = { true },
    private var blockExcept: suspend T.(e: Throwable) -> Unit = { e: Throwable -> throw e }
): ICorChainDsl<T>, ICorHandlerDsl<T> {
    override fun build(): ICorExec<T> = CorParallel<T>(
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept
    )

    override fun add(worker: ICorExecDsl<T>) {
        workers.add(worker)
    }

    override fun on(function: suspend T.() -> Boolean) {
        blockOn = function
    }

    override fun except(function: suspend T.(e: Throwable) -> Unit) {
        blockExcept = function
    }
}
