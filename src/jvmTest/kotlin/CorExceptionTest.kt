import com.crowdproj.kotlin.cor.TestContext
import com.crowdproj.kotlin.cor.handlers.chain
import com.crowdproj.kotlin.cor.handlers.worker
import com.crowdproj.kotlin.cor.rootChain
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CorExceptionTest {

    @Test
    fun `on exception - positive`() {
        val testText = "test1"
        val ctx = TestContext()
        val chain = rootChain<TestContext> {
            worker {
                handle { throw RuntimeException(testText) }
                except { e -> text = e.message ?: "" }
            }
        }.build()

        runBlocking { chain.exec(ctx) }

        assertEquals(testText, ctx.text)
    }

    @Test
    fun `two exception - negative`() {
        val testText1 = "test1"
        val testText2 = "test2"
        val ctx = TestContext()
        val chain = rootChain<TestContext> {
            chain {
                worker {
                    handle { throw RuntimeException(testText1) }
                    except { e -> text = e.message ?: "" }
                }
                except { e -> text = e.message ?: "" }
            }
        }.build()

        runBlocking { chain.exec(ctx) }

        assertNotEquals(testText2, ctx.text)
    }

    @Test
    fun `two exception - positive`() {
        val testText1 = "test1"
        val testText2 = "test2"
        val ctx = TestContext()
        val chain = rootChain<TestContext> {
            chain {
                worker {
                    handle { throw RuntimeException(testText1) }
                    except { e ->
                        text = e.message ?: ""
                        throw e
                    }
                }
                except { e -> text = "${e.message}$testText2" ?: "" }
            }
        }.build()

        runBlocking { chain.exec(ctx) }

        assertEquals("$testText1$testText2", ctx.text)
    }

}
