package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.Const.LOOP_MAX_EXCEPTION
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
fun <T> ICorChainDsl<T>.loopDoWhile(
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
    val blockRestarts: () -> Long = { 0L },
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {
    private var numException = 0L
    private var maxException = LOOP_MAX_EXCEPTION

    init {
        maxException = blockRestarts.invoke()
    }

    override suspend fun handle(context: T) {
        when (checkBefore) {
            true -> loopWhile(context)
            false -> loopDoWhile(context)
        }
    }

    private suspend fun loopWhile(context: T) {
        while (blockCheck.invoke(context) && numException <= maxException - 1) {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
                numException++
            }
        }
    }

    private suspend fun loopDoWhile(context: T) {
        do {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
                numException++
            }
        } while (blockCheck.invoke(context) && numException <= maxException - 1)
    }

}

@CorDslMarker
class CorLoopDsl<T>(
    private val checkBefore: Boolean,
    var blockRestarts: () -> Long = { LOOP_MAX_EXCEPTION },
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChainDsl<T>() {
    override fun build(): ICorExec<T> = CorLoop(
        checkBefore,
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockRestarts = blockRestarts,
        blockCheck = blockCheck,
    )

    /**
     * Maximum allowed number of exceptions
     * If the value is less than or equal to zero, the number of exceptions is unlimited
     */
    fun restarts(function: () -> Long) {
        blockRestarts = function
    }

    /**
     * Cycle repetition condition
     * Repeat until the condition is met
     */
    fun check(function: suspend T.() -> Boolean) {
        blockCheck = function
    }

}

