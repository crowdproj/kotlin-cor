import com.crowdproj.kotlin.cor.CorBaseTest
import com.crowdproj.kotlin.cor.CorStatuses
import com.crowdproj.kotlin.cor.TestContext
import com.crowdproj.kotlin.cor.handlers.loopDoWhile
import com.crowdproj.kotlin.cor.handlers.worker
import com.crowdproj.kotlin.cor.rootChain
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
    fun loopCorTest() {
        val chain = rootChain<TestContext> {
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

        val ctx = TestContext(some = 1)

        runBlocking { chain.exec(ctx) }

        assertNotEquals(CorStatuses.FAILING, ctx.status)
        assertEquals(10, ctx.some)
    }

}
