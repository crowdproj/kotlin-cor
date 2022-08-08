package com.crowdproj.kotlin.cor.base

import com.crowdproj.kotlin.cor.*

abstract class BaseCorChainDsl<T,K>(
    override var title: String = "",
    override var description: String = "",
    protected val workers: MutableList<ICorExecDsl<K>> = mutableListOf(),
    protected var blockOn: suspend T.() -> Boolean = { true },
    protected var blockExcept: suspend T.(e: Throwable) -> Unit = { e: Throwable -> throw e },
) : ICorExecDsl<T>, ICorOnDsl<T>, ICorExceptDsl<T>, ICorAddExecDsl<K> {

    abstract override fun build(): ICorExec<T>

    override fun add(worker: ICorExecDsl<K>) {
        workers.add(worker)
    }

    override fun on(function: suspend T.() -> Boolean) {
        blockOn = function
    }

    override fun except(function: suspend T.(e: Throwable) -> Unit) {
        blockExcept = function
    }
}
