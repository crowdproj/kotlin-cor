import kotlinx.coroutines.runBlocking
import com.crowdproj.kotlin.cor.CorBaseTest
import com.crowdproj.kotlin.cor.TestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class CorJvmTest {

    @Test
    fun corTest() {
        val chain = CorBaseTest.chain
        val ctx = TestContext(some = 13)

        runBlocking { chain.exec(ctx) }

        assertEquals(17, ctx.some)
    }
}
