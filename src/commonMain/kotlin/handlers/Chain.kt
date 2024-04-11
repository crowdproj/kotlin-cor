package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorAddExecDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl

@CorDslMarker
fun <T,C> ICorAddExecDsl<T,C>.chain(function: CorChainDsl<T,C>.() -> Unit) {
    add(CorChainDsl<T,C>(this.config).apply(function))
}

class CorChain<T>(
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
    override suspend fun handle(context: T) {
        execs.forEach { it.exec(context) }
    }
}

/**
 * DLS is the execution context of multiple chains.
 * It can be expanded by other chains.
 * The chains are executed sequentially.
 */
@CorDslMarker
class CorChainDsl<T,C>(config: C) : BaseCorChainDsl<T,T,C>(config) {
    override fun build(): ICorExec<T> = CorChain(
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept
    )
}
