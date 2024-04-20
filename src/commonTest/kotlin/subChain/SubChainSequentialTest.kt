package com.crowdproj.kotlin.cor.subChain

import com.crowdproj.kotlin.cor.handlers.subChain
import com.crowdproj.kotlin.cor.handlers.worker
import com.crowdproj.kotlin.cor.helper.TestContext
import com.crowdproj.kotlin.cor.helper.TestSubContext
import com.crowdproj.kotlin.cor.rootChain
import kotlinx.atomicfu.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.measureTime

class SubChainSequentialTest {
    @Test
    fun sequentialWorkers() = runTest {
        withContext(Dispatchers.Default) {
            val ctx = TestContext(text = "ab", some = 1)
            val t = measureTime {
                chain.exec(ctx)
            }
            println("TIME: $t")
            assertEquals("a_w1_w2b_w1_w2", ctx.text)
        }
    }

    @Test
    fun sequentialData() = runTest {
        withContext(Dispatchers.Default) {
            val ctx = TestContext(text = "0123456789", some = 2)
            val t = measureTime {
                chain.exec(ctx)
            }
            println("TIME: $t")
            assertEquals("0;1;2;3;4;5;6;7;8;9;", ctx.atomicText.value)
        }
    }

    @Test
    fun parallelData() = runTest {
        val symbols = "0123456789"
        withContext(Dispatchers.Default) {
            val ctx = TestContext(text = symbols, some = 3)
            val t = measureTime {
                chain.exec(ctx)
            }
            println("TIME: $t")
            // In parallel mode the order is not guaranteed
            symbols.forEach {
                assertContains(ctx.atomicText.value, "$it;")
            }

        }
    }

    companion object {
        val chain = rootChain<TestContext> {
            subChain<TestContext, TestSubContext, Unit> {
                title = "Check sequential execution of workers"
                on { some == 1 }
                split {
                    val str = text
                    text = ""
                    str.map { TestSubContext(str = it.toString(), parent = this) }.asFlow()
                }
                worker("") { delay(200); str += "_w1" }
                worker("") { delay(20); str += "_w2" }
                join {
                    text += it.str
                }
            }
            subChain<TestContext, TestSubContext, Unit> {
                title = "Check sequential execution of data"
                on { some == 2 }
                buffer(0)
                split {
                    val str = text
                    text = ""
                    str.map { TestSubContext(str = it.toString(), parent = this) }.asFlow()
                }
                worker("") { println("START: $str") }
                worker("") { val del = 200 - str.toLong() * 20; println("$str $del"); delay(del); str += ";" }
                worker("") { parent.atomicText.update { it + str } }
                worker("") { println("STOP: $str") }
            }
            subChain<TestContext, TestSubContext, Unit> {
                title = "Check parallel execution of data"
                buffer(11)
                on { some == 3 }
                split {
                    val str = text
                    text = ""
                    str.map { TestSubContext(str = it.toString(), parent = this) }.asFlow()
                }
                worker("") { val del = 20 - str.toLong() * 2; println("$str $del"); delay(del); str += ";" }
                worker("") { parent.atomicText.update { it + str } }
            }
        }.build()
    }
}
