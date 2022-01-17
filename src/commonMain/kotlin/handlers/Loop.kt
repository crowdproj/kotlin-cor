package com.crowdproj.kotlin.cor.handlers

import com.crowdproj.kotlin.cor.CorDslMarker
import com.crowdproj.kotlin.cor.ICorChainDsl
import com.crowdproj.kotlin.cor.ICorExec
import com.crowdproj.kotlin.cor.base.BaseCorChain
import com.crowdproj.kotlin.cor.base.BaseCorChainDsl
import kotlinx.coroutines.delay

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
    val blockMaxException: () -> Long = { 0L },
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChain<T>(
    title = title,
    description = description,
    blockOn = blockOn,
    blockExcept = blockExcept
) {
    var numException = 0L
    var maxException = 0L

    init {
        maxException = blockMaxException.invoke()
    }

    override suspend fun handle(context: T) {
        when (checkBefore) {
            true -> loopWhile(context)
            false -> loopDoWhile(context)
        }
    }

    private suspend fun loopWhile(context: T) {
        while (blockCheck.invoke(context) && numException <= maxException) {
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
            println(blockCheck.invoke(context))
            println(numException <= maxException)
            println(numException)
            println(maxException)
            println("-----")
            delay(500)
        } while (blockCheck.invoke(context) && numException <= maxException)
    }

}

@CorDslMarker
class CorLoopDsl<T>(
    private val checkBefore: Boolean,
    var blockMax: () -> Long = { 0L },
    var blockCheck: suspend T.() -> Boolean = { true },
) : BaseCorChainDsl<T>() {
    override fun build(): ICorExec<T> = CorLoop(
        checkBefore,
        title = title,
        description = description,
        execs = workers.map { it.build() }.toList(),
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockMaxException = blockMax,
        blockCheck = blockCheck,
    )

    /**
     * Maximum allowed number of exceptions
     * If the value is less than or equal to zero, the number of exceptions is unlimited
     */
    fun restarts(function: () -> Long) {
        blockMax = function
    }

    /**
     * Cycle repetition condition
     * Repeat until the condition is met
     */
    fun check(function: suspend T.() -> Boolean) {
        blockCheck = function
    }

}

