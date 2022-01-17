import com.crowdproj.kotlin.cor.CorBaseTest
import com.crowdproj.kotlin.cor.CorStatuses
import com.crowdproj.kotlin.cor.LoopCorBaseTest
import com.crowdproj.kotlin.cor.TestContext
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CorJvmTest {

    @Test
    fun corTest() {
        val chain = CorBaseTest.chain
        val ctx = TestContext(some = 13)

        runBlocking { chain.exec(ctx) }

        assertEquals(17, ctx.some)
    }

    @Test
    fun loopDoWhileCorTest() {
        val chain = LoopCorBaseTest.loopDoWhile

        val ctx = TestContext(some = 1)

        runBlocking { chain.exec(ctx) }

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(10, ctx.some)
    }

    @Test
    fun loopWhileCorTest() {
        val chain = LoopCorBaseTest.loopWhile

        val ctx = TestContext(some = 1)

        runBlocking { chain.exec(ctx) }

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(10, ctx.some)
    }

    @Test
    fun exceptionLoopWhileCorTest() {
        val chain = LoopCorBaseTest.exceptionLoopWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(4, ctx.some)
    }

    @Test
    fun exceptionLoopDoWhileCorTest() {
        val chain = LoopCorBaseTest.exceptionLoopDoWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(4, ctx.some)
    }

    @Test
    fun zeroExceptionLoopDoWhileCorTest() {
        val chain = LoopCorBaseTest.zeroExceptionLoopDoWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(1, ctx.some)
    }

    @Test
    fun zeroExceptionLoopWhileCorTest() {
        val chain = LoopCorBaseTest.zeroExceptionLoopWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(0, ctx.some)
    }

    @Test
    fun lessThanZeroExceptionLoopDoWhileCorTest() {
        val chain = LoopCorBaseTest.lessThanZeroExceptionLoopDoWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(7, ctx.some)
    }

    @Test
    fun lessThanZeroExceptionLoopWhileCorTest() {
        val chain = LoopCorBaseTest.lessThanZeroExceptionLoopWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(7, ctx.some)
    }

}
