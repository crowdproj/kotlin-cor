package com.crowdproj.kotlin.cor

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CorTest {

    @Test
    fun corTest() = runTest {
        val chain = CorBaseTest.chain
        val ctx = TestContext(some = 13)

        chain.exec(ctx)

        assertEquals(17, ctx.some)
    }

    @Test
    fun loopUntilCorTest() = runTest {
        val chain = LoopCorBaseTest.loopUntil

        val ctx = TestContext(some = 1)

        chain.exec(ctx)

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(10, ctx.some)
    }

    @Test
    fun loopWhileCorTest() = runTest {
        val chain = LoopCorBaseTest.loopWhile

        val ctx = TestContext(some = 1)

        chain.exec(ctx)

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(10, ctx.some)
    }

}
