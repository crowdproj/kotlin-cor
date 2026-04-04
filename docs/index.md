# Documentation

## Overview

- [Core Concepts](concepts.md) — Worker, Chain, Parallel, Loop
- [DSL Reference](dsl.md) — Full DSL API documentation
- [Examples](examples.md) — From simple to advanced
- [Comparison](comparison.md) — How kotlin-cor compares to alternatives

## Getting Started

### Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.crowdproj:kotlin-cor:${VERSION}")
}
```

### Quick Start

```kotlin
import com.crowdproj.kotlin.cor.*

data class Context(
    var status: String = "NONE",
    var data: String? = null
)

val chain = rootChain<Context> {
    worker {
        title = "Initialize"
        on { status == "NONE" }
        handle { status = "RUNNING" }
    }
    worker {
        title = "Process"
        on { status == "RUNNING" }
        handle { data = "processed" }
    }
}.build()

runBlocking { chain.exec(Context()) }
```

## Why kotlin-cor?

kotlin-cor is designed for developers who want:
- **Readable business logic** — code that reads like a specification
- **Full control** — no visual designers or XML schemas
- **Easy testing** — each worker is a testable unit
- **KMP support** — single codebase for all platforms