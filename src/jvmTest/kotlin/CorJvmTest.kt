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
        assertEquals(5, ctx.some)
    }

    @Test
    fun exceptionLoopDoWhile() {
        val chain = LoopCorBaseTest.exceptionLoopDoWhile

        val ctx = TestContext(some = 0)

        runBlocking { chain.exec(ctx) }

        assertEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(5, ctx.some)
    }

}
