package com.crowdproj.kotlin.cor.helper

data class TestSubContext(
    var temp: Int = Int.MIN_VALUE,
    var str: String = "",
    val parent: TestContext = TestContext(),
)
