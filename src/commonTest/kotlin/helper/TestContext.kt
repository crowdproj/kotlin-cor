package com.crowdproj.kotlin.cor.helper

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

data class TestContext(
    var status: CorStatuses = CorStatuses.NONE,
    var some: Int = Int.MIN_VALUE,
    var text: String = "",
    val atomicText: AtomicRef<String> = atomic("")
) {

}
