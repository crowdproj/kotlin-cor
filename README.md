[![Download](https://img.shields.io/maven-central/v/com.crowdproj/kotlin-cor)](https://search.maven.org/artifact/com.crowdproj/kotlin-cor)
[![License](https://img.shields.io/github/license/crowdproj/kotlin-cor)](LICENSE)

# kotlin-cor — Chain of Responsibility for Business Logic

**kotlin-cor** is a lightweight Kotlin Multiplatform library that brings Chain of Responsibility pattern to business logic orchestration. It allows you to write complex business processes as readable, declarative code — instead of XML schemas or visual BPM designers.

## Niche & Purpose

kotlin-cor fills the gap between simple CoR pattern and full-blown workflow engines:

| Category | Tools | kotlin-cor Role |
|----------|-------|-----------------|
| **State Machines** | Tinder/StateMachine, KStateMachine | Different — sequential processing, not state transitions |
| **Workflow Engines** | Temporal, Infinitic, Conductor | Lighter alternative — no infrastructure needed |
| **Saga Pattern** | Arrow Saga | Different — sequential pipeline, not distributed transactions |
| **BPMS** | Camunda, Flowable | **Direct competitor** — code-first instead of visual-first |

**Primary Use Cases:**
- Request handling pipelines (validation → authorization → processing → response)
- Business logic in microservices without external orchestration
- Projects requiring Kotlin Multiplatform (JVM + JS + Native)
- Teams wanting version control over business logic

## Competitive Advantages

1. **Code-first** — Business logic in Kotlin, not XML/BPMN
2. **No infrastructure** — Just a library, no external servers
3. **KMP** — Single codebase for all platforms (JVM, JS, Native, Wasm)
4. **Readable** — Chain reads like documentation
5. **Extensible** — Easy to add custom handlers

See [docs/comparison.md](docs/comparison.md) for detailed analysis.

## Quick Example

```kotlin
val chain = rootChain<BizContext> {
    validate("Validate request") {
        validateNotEmpty("Check ID not empty")
        validateFormat("Check ID format")
    }
    authorize("Check access rights") {
        fetchUserPermissions()
        checkObjectAccess()
    }
    fetchData("Load business object")
    respond("Prepare response")
}.build()

runBlocking { chain.exec(BizContext(request)) }
```

## Key Features

- **Human-readable** — business logic reads like a specification
- **Kotlin Multiplatform** — JVM, JS, Native, Wasm
- **Condition-based execution** — `on { condition }` for conditional steps
- **Error handling** — `except` blocks for compensation/rollback
- **Parallel execution** — `parallel` block for concurrent operations
- **Settings support** — external configuration injection

## Installation

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.crowdproj:kotlin-cor:${VERSION}")
}
```

## Documentation

- [Overview](docs/index.md)
- [Core Concepts](docs/concepts.md) — Worker, Chain, Parallel, Loop
- [DSL Reference](docs/dsl.md)
- [Examples](docs/examples.md)
- [Contributing](docs/contributing.md)

## Status

- **Version**: ${VERSION}
- **Kotlin**: 2.3.20
- **Platforms**: JVM, JS, Linux, iOS, macOS, tvOS, watchOS, Wasm, Windows, Android Native