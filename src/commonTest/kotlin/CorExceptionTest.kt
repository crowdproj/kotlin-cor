package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.chain
import com.crowdproj.kotlin.cor.handlers.worker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CorExceptionTest {

    @Test
    @JsName("on_exception_positive")
    fun `on exception - positive`() = runTest {
        val testText = "test1"
        val ctx = TestContext()
        val chain = rootChain<TestContext> {
            worker {
                handle { throw RuntimeException(testText) }
                except { e -> text = e.message ?: "" }
            }
        }.build()

        chain.exec(ctx)

        assertEquals(testText, ctx.text)
    }

    @Test
    @JsName("two_exception_negative")
    fun `two exception - negative`() = runTest {
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

        chain.exec(ctx)

        assertNotEquals(testText2, ctx.text)
    }

    @Test
    @JsName("two_exceptions_positive")
    fun `two exceptions - positive`() = runTest {
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
                except { e -> text = "${e.message}$testText2" }
            }
        }.build()

        chain.exec(ctx)

        assertEquals("$testText1$testText2", ctx.text)
    }

}
