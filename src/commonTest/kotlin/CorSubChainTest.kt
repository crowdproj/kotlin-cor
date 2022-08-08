package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.subChain
import com.crowdproj.kotlin.cor.handlers.worker
import com.crowdproj.kotlin.cor.helper.CorStatuses
import com.crowdproj.kotlin.cor.helper.TestContext
import com.crowdproj.kotlin.cor.helper.TestSubContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CorSubChainTest {
    @Test
    fun createCor() = runTest {
        val ctx = TestContext()
        chain.exec(ctx)
        assertEquals(CorStatuses.RUNNING, ctx.status)
        assertEquals(65, ctx.some)
    }

    companion object {
        val chain = rootChain<TestContext> {
            worker("status") { status = CorStatuses.RUNNING }
            worker("init") {
                some = 0
            }
            subChain<TestContext, TestSubContext> {
                buffer(20)
                on { status == CorStatuses.RUNNING }
                split { (1..10).asFlow().map { TestSubContext(temp = it) } }
                worker {
                    handle {
                        temp ++
                    }
                }
                join { sub: TestSubContext ->
                    some += sub.temp
                }
            }
        }.build()
    }
}
