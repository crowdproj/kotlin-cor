package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.loopUntil
import com.crowdproj.kotlin.cor.handlers.loopWhile
import com.crowdproj.kotlin.cor.handlers.worker
import kotlin.test.Test

class LoopCorBaseTest {
    @Test
    fun createCor() {
    }

    companion object {
        val loopUntil = rootChain<TestContext> {
            loopUntil {
                title = "Looping"
                description = "Repeat the business chain until the condition is met"

                on { true }
                check { some < 5 }
                except { status = CorStatuses.FAILING }
                restarts { 5L }
                worker(title = "Increment some") {
                    some++
                    println("=$some")
                }
            }
            loopUntil {
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                worker(title = "Increment some") {
                    some++
                    println("=$some")
                }
            }
        }.build()

        val loopWhile = rootChain<TestContext> {
            loopWhile {
                title = "Looping"
                description = "Repeat the business chain until the condition is met"

                on { true }
                check { some < 5 }
                except { status = CorStatuses.FAILING }
                restarts { 5L }
                worker(title = "Increment some") {
                    some++
                    println("=$some")
                }
            }
            loopWhile {
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                worker(title = "Increment some") {
                    some++
                    println("=$some")
                }
            }
        }.build()

        val exceptionLoopWhile = rootChain<TestContext> {
            loopWhile {
                check { some < 10 } // выполняется пока true
                except { status = CorStatuses.FAILING }
                restarts { 5L } // возможное количество исключений
                failed { some-- }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val exceptionLoopUntil = rootChain<TestContext> {
            loopUntil {
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                restarts { 5L }
                failed { some-- }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val zeroExceptionLoopUntil = rootChain<TestContext> {
            loopUntil {
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                restarts { 0L }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val zeroExceptionLoopWhile = rootChain<TestContext> {
            loopWhile {
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                restarts { 0L }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val lessThanZeroExceptionLoopUntil = rootChain<TestContext> {
            loopUntil {
                check { some < 6 }
                except { status = CorStatuses.FAILING }
                restarts { -1L }
                failed { some++ }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val lessThanZeroExceptionLoopWhile = rootChain<TestContext> {
            loopWhile {
                check { some < 6 }
                except { status = CorStatuses.FAILING }
                restarts { -1L }
                failed { some++ }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

    }
}
