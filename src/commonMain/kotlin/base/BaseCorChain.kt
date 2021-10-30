package com.crowdproj.kotlin.cor.base

import com.crowdproj.kotlin.cor.ICorWorker

abstract class BaseCorChain<T>(
    override val title: String,
    override val description: String = "",
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorWorker<T> {

    override suspend fun on(context: T): Boolean = blockOn(context)
    override suspend fun except(context: T, e: Throwable) = blockExcept(context, e)

    abstract override suspend fun handle(context: T): Unit
}
