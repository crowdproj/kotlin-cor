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

    }
}
