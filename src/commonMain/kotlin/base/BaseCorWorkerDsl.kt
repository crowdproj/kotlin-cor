package com.crowdproj.kotlin.cor.base

import com.crowdproj.kotlin.cor.*

abstract class BaseCorWorkerDsl<T,C>(
    override val config: C,
    override var title: String = "",
    override var description: String = "",
    protected var blockOn: suspend T.() -> Boolean = { true },
    protected var blockHandle: suspend T.() -> Unit = {},
    protected var blockExcept: suspend T.(e: Throwable) -> Unit = { e: Throwable -> throw e },
) : ICorExecDsl<T,C>, ICorOnDsl<T>, ICorExceptDsl<T>, ICorHandleDsl<T,C> {

    abstract override fun build(): ICorExec<T>

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
