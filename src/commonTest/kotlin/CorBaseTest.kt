package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.chain
import com.crowdproj.kotlin.cor.handlers.parallel
import com.crowdproj.kotlin.cor.handlers.worker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CorBaseTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createCor() = runTest {
        val ctx = TestContext(some = 0)
        chain.exec(ctx)
        assertEquals(CorStatuses.RUNNING, ctx.status)
        assertEquals(5, ctx.some)
        assertEquals("", ctx.text)
    }

    companion object {
        val chain = rootChain<TestContext> {
            worker {
                title = "Status initialization"
                description = "Check the status initialization at the buziness chain start"

                on { status == CorStatuses.NONE }
                handle { status = CorStatuses.RUNNING }
                except { status = CorStatuses.FAILING }
            }

            chain {
                on { status == CorStatuses.RUNNING }

                worker(
                    title = "Lambda worker",
                    description = "Example of a buziness chain worker in a lambda form"
                ) {
                    some += 4
                }
            }

            parallel {
                on {
                    some < 15
                }
                worker(title = "Increment some") {
                    some++
                }
            }
            printResult()

        }.build()

    }
}

private fun ICorChainDsl<TestContext>.printResult() = worker(title = "Print example") {
    println("some = $some")
}

data class TestContext(
    var status: CorStatuses = CorStatuses.NONE,
    var some: Int = Int.MIN_VALUE,
    var text: String = "",
) {

}

enum class CorStatuses {
    NONE,
    RUNNING,
    FAILING,
}
