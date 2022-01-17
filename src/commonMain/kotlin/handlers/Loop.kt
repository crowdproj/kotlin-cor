package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.Const.LOOP_MAX_EXCEPTION
import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorChainDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl
import kotlin.math.absoluteValue

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
    val blockMaxEx: () -> Long = { LOOP_MAX_EXCEPTION },
    var blockCheck: suspend T.() -> Boolean = { true },
    var blockFailed: suspend T.() -> Unit = {},
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {
    private var numException = 0L
    private var maxException = LOOP_MAX_EXCEPTION

    init {
        maxException = blockMaxEx.invoke().takeIf { it >= 0 } ?: blockMaxEx.invoke().absoluteValue
    }

    override suspend fun handle(context: T) {
        when (checkBefore) {
            true -> loopWhile(context)
            false -> loopUntil(context)
        }
    }

    private suspend fun loopWhile(context: T) {
        while (blockCheck.invoke(context)
            && (numException < maxException
                    || (numException == 0L && maxException == 0L))
        ) {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
                numException++
            }
        }
        if (numException >= maxException) {
            blockFailed.invoke(context)
        }
    }

    private suspend fun loopUntil(context: T) {
        do {
            try {
                execs.forEach { it.exec(context) }
            } catch (e: Throwable) {
                except(context, e)
                numException++
            }
        } while (blockCheck.invoke(context)
            && (numException < maxException
                    || (numException == 0L && maxException == 0L))
        )
        if (numException >= maxException) {
            blockFailed.invoke(context)
        }
    }

}

@CorDslMarker
class CorLoopDsl<T>(
    private val checkBefore: Boolean,
    var blockMaxEx: () -> Long = { LOOP_MAX_EXCEPTION },
    var blockCheck: suspend T.() -> Boolean = { true },
    var blockFailed: suspend T.() -> Unit = {},
) : BaseCorChainDsl<T>() {
    override fun build(): ICorExec<T> = CorLoop(
        checkBefore,
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockMaxEx = blockMaxEx,
        blockCheck = blockCheck,
        blockFailed = blockFailed,
    )

    /**
     * Maximum allowed number of exceptions
     * If the value is less than zero, then the number of exceptions is unlimited
     */
    fun maxEx(function: () -> Long) {
        blockMaxEx = function
    }

    /**
     * Cycle repetition condition
     * Repeat until the condition is met
     */
    fun check(function: suspend T.() -> Boolean) {
        blockCheck = function
    }

    /**
     * Executed when the allowed number of exceptions is exceeded
     */
    fun failed(function: suspend T.() -> Unit) {
        blockFailed = function
    }

}

