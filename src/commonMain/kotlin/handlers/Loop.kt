package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorChainDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl

@CorDslMarker
fun <T> ICorChainDsl<T>.loopWhile(
    function: CorLoopDsl<T>.() -> Unit
) {
    add(
        CorLoopDsl<T>(checkBefore = true).apply(function)
    )
}

@CorDslMarker
fun <T> ICorChainDsl<T>.loopUntil(
    function: CorLoopDsl<T>.() -> Unit
) {
    add(
        CorLoopDsl<T>(checkBefore = false).apply(function)
    )
}

class CorLoop<T>(
    private val checkBefore: Boolean,
    private val execs: List<ICorExec<T>>,
    title: String,
    description: String = "",
    blockOn: suspend T.() -> Boolean = { true },
    blockExcept: suspend T.(Throwable) -> Unit = {},
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {

    override suspend fun handle(context: T) {
        when (checkBefore) {
            true -> loopWhile(context)
            false -> loopUntil(context)
        }
    }

    private suspend fun loopWhile(context: T) {
        while (blockCheck.invoke(context)) {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
            }
        }
    }

    private suspend fun loopUntil(context: T) {
        do {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
            }
        } while (blockCheck.invoke(context))
    }

}

@CorDslMarker
class CorLoopDsl<T>(
    private val checkBefore: Boolean,
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChainDsl<T>() {
    override fun build(): ICorExec<T> = CorLoop(
        checkBefore,
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockCheck = blockCheck,
    )

    /**
     * Cycle repetition condition
     * Repeat until the condition is met
     */
    fun check(function: suspend T.() -> Boolean) {
        blockCheck = function
    }

}

