package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.loopDoWhile
import com.crowdproj.kotlin.cor.handlers.loopWhile
import com.crowdproj.kotlin.cor.handlers.worker
import kotlin.test.Test

class LoopCorBaseTest {
    @Test
    fun createCor() {
    }

    companion object {
        val loopDoWhile = rootChain<TestContext> {
            loopDoWhile {
                title = "Looping"
                description = "Repeat the business chain until the condition is met"

                on { true }
                restarts { 5L }
                check { some < 5 }
                except { status = CorStatuses.FAILING }
                worker(title = "Increment some") {
                    some++
                    println("=$some")
                }
            }
            loopDoWhile {
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
                restarts { 5L }
                check { some < 5 }
                except { status = CorStatuses.FAILING }
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
                restarts { 5L } // возможное количество исключений
                check { some < 10 } // выполняется пока true
                except { status = CorStatuses.FAILING }
                failed { some-- }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val exceptionLoopDoWhile = rootChain<TestContext> {
            loopDoWhile {
                restarts { 5L }
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                failed { some-- }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val zeroExceptionLoopDoWhile = rootChain<TestContext> {
            loopDoWhile {
                restarts { 0L }
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val zeroExceptionLoopWhile = rootChain<TestContext> {
            loopWhile {
                restarts { 0L }
                check { some < 10 }
                except { status = CorStatuses.FAILING }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val lessThanZeroExceptionLoopDoWhile = rootChain<TestContext> {
            loopDoWhile {
                restarts { -1L }
                check { some < 6 }
                except { status = CorStatuses.FAILING }
                failed { some++ }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

        val lessThanZeroExceptionLoopWhile = rootChain<TestContext> {
            loopWhile {
                restarts { -1L }
                check { some < 6 }
                except { status = CorStatuses.FAILING }
                failed { some++ }
                worker(title = "Increment some") {
                    some++
                    throw RuntimeException("ex loop")
                }
            }
        }.build()

    }
}
