package com.crowdproj.kotlin.cor

import com.crowdproj.kotlin.cor.handlers.worker
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CorConfigTest {

    @Test
    fun corConfigTest() = runTest {
        val ctx = TestContext()

        chain.exec(ctx)

        assertEquals("my-setting", ctx.history)
    }
    companion object {
        val chain = rootChain<TestContext, TestConfig>(TestConfig("my-setting")) {
            worker {
                title = "configTest"
                description = "testing for work with initial config"
                handle {
                    history += this@rootChain.config.setting
                }
            }
        }.build()

        data class TestConfig(
            val setting: String = ""
        )
        data class TestContext(
            var history: String = ""
        )
    }
}
