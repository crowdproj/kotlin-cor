package com.crowdproj.kotlin.cor.helper

data class TestContext(
    var status: CorStatuses = CorStatuses.NONE,
    var some: Int = Int.MIN_VALUE,
    var text: String = "",
) {

}
