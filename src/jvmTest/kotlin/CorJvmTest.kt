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
    fun loopUntilCorTest() {
        val chain = LoopCorBaseTest.loopUntil

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

}
