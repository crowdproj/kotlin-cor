package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorChainDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@CorDslMarker
fun <T> ICorChainDsl<T>.parallel(function: CorParallelDsl<T>.() -> Unit) {
    add(CorParallelDsl<T>().apply(function))
}

class CorParallel<T>(
    private val execs: List<ICorExec<T>>,
    title: String,
    description: String = "",
    blockOn: suspend T.() -> Boolean = { true },
    blockExcept: suspend T.(Throwable) -> Unit = {},
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {

    override suspend fun handle(context: T): Unit = coroutineScope {
        execs
            .map { launch { it.exec(context) } }
            .toList()
            .forEach { it.join() }
    }
}

/**
 * DLS is the execution context of multiple chains.
 * It can be expanded by other chains.
 * Chains are started simultaneously and executed in parallel.
 */
@CorDslMarker
class CorParallelDsl<T>(): BaseCorChainDsl<T>() {
    override fun build(): ICorExec<T> = CorParallel(
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept
    )
}
