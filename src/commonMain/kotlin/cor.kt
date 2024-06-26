package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.CorChainDsl
import com.crowdproj.kotlin.cor.handlers.CorWorkerDsl

interface ICorConfigurable<C> {
    val config: C
}

interface ICorExecDsl<T,C>: ICorConfigurable<C> {
    var title: String
    var description: String
    fun build(): ICorExec<T>
}

interface ICorOnDsl<T> {
    fun on(function: suspend T.() -> Boolean)
}

interface ICorExceptDsl<T> {
    fun except(function: suspend T.(e: Throwable) -> Unit)
}

interface ICorAddExecDsl<T,C>: ICorConfigurable<C> {
    fun add(worker: ICorExecDsl<T,C>)
}

interface ICorHandleDsl<T,C> {
    fun handle(function: suspend T.() -> Unit)
}

interface ICorExec<T> {
    suspend fun exec(context: T)
}

interface ICorWorker<T> : ICorExec<T> {
    val title: String
    val description: String
    suspend fun on(context: T): Boolean
    suspend fun except(context: T, e: Throwable)
    suspend fun handle(context: T)

    override suspend fun exec(context: T) {
        if (on(context)) {
            try {
                handle(context)
            } catch (e: Throwable) {
                except(context, e)
            }
        }
    }
}

fun <T> rootChain(function: CorChainDsl<T,Unit>.() -> Unit) = CorChainDsl<T,Unit>(Unit).apply(function)
fun <T,C> rootChain(config: C, function: CorChainDsl<T,C>.() -> Unit) = CorChainDsl<T,C>(config).apply(function)
fun <T> rootWorker(function: CorWorkerDsl<T,Unit>.() -> Unit) = CorWorkerDsl<T,Unit>(Unit).apply(function)
fun <T,C> rootWorker(config: C, function: CorWorkerDsl<T,C>.() -> Unit) = CorWorkerDsl<T,C>(config).apply(function)

