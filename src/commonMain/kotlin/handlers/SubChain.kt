package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorAddExecDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@CorDslMarker
fun <T, K> ICorAddExecDsl<T>.subChain(function: CorSubChainDsl<T,K>.() -> Unit) {
    add(CorSubChainDsl<T,K>().apply(function))
}

class CorSubChain<T,K>(
    private val execs: List<ICorExec<K>>,
    title: String,
    description: String = "",
    blockOn: suspend T.() -> Boolean = { true },
    private val blockSplit: suspend T.() -> Flow<K>,
    private val blockJoin: suspend T.(K) -> Unit,
    blockExcept: suspend T.(Throwable) -> Unit = {},
    private val buffer: Int = 1,
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {

    override suspend fun handle(context: T): Unit = coroutineScope {
        context
            .blockSplit()
            .onEach { subCtx -> execs.map{ launch { it.exec(subCtx) } }.forEach { it.join() } }
            .buffer(buffer)
            .collect { context.blockJoin(it) }
    }
}

/**
 * DLS is the execution context of multiple chains.
 * It can be expanded by other chains.
 */
@CorDslMarker
class CorSubChainDsl<T,K>(
): BaseCorChainDsl<T,K>() {
    private var blockSplit: suspend T.() -> Flow<K> = { emptyFlow() }
    private var blockJoin: suspend T.(K) -> Unit = {}
    private var bufferSize: Int = 0

    fun buffer(size: Int) {
        bufferSize = size
    }

    fun split(funSplit: suspend T.() -> Flow<K>) {
        blockSplit = funSplit
    }

    fun join(funJoin: suspend T.(K) -> Unit) {
        blockJoin = funJoin
    }

    override fun build(): ICorExec<T> = CorSubChain(
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockSplit = blockSplit,
        blockJoin = blockJoin,
        buffer = bufferSize
    )
}