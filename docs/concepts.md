# Core Concepts

## Architecture Overview

kotlin-cor consists of three main building blocks:

1. **Worker** — single execution step
2. **Chain** — sequential execution of workers
3. **Parallel** — concurrent execution of workers
4. **Loop** — conditional repetition

## Worker

A worker is the smallest unit of execution. Each worker has:

- **title** — human-readable name
- **description** — optional documentation
- **on** — condition for execution (default: always)
- **handle** — execution logic
- **except** — error handling (compensation)

```kotlin
worker {
    title = "Validate request"
    description = "Check input data"
    
    on { input.isNotBlank() }
    handle { result = input.uppercase() }
    except { e -> error("Validation failed: $e") }
}
```

### Short form

```kotlin
worker(title = "Quick worker") {
    data += 1
}
```

## Chain

Chains group multiple workers that execute sequentially.

```kotlin
chain {
    title = "Validation chain"
    
    worker { title = "Check ID"; on { id.isNotEmpty() } }
    worker { title = "Check format"; on { id.matches(regex) } }
    worker { title = "Normalize"; handle { id = id.trim() } }
}
```

## Parallel

Parallel executes workers concurrently — order is not guaranteed.

```kotlin
parallel {
    on { featureEnabled }
    
    worker { title = "Fetch user"; user = fetchUser(id) }
    worker { title = "Fetch permissions"; permissions = fetchPermissions(id) }
    worker { title = "Fetch history"; history = fetchHistory(id) }
}
```

## Loop

Loops provide iteration with conditions:

```kotlin
// While condition is true
loopWhile {
    check { attempts < maxRetries }
    worker { title = "Retry" }
}

// Until condition becomes true
loopUntil {
    check { result.isReady }
    worker { title = "Check" }
}
```

## Context

The context holds the data that flows through the chain:

```kotlin
data class BizContext(
    var status: String = "INIT",
    var input: String = "",
    var output: String? = null
)

val chain = rootChain<BizContext> {
    worker {
        title = "Process"
        handle { output = input.uppercase() }
    }
}.build()

runBlocking { chain.exec(BizContext(input = "hello")) }
```

## Settings

External configuration can be injected via settings:

```kotlin
data class AppSettings(
    val logger: Logger,
    val db: Database
)

val chain = rootChain<BizContext, AppSettings>(settings) {
    worker {
        handle { 
            config.logger.info("Processing")
            config.db.save(result)
        }
    }
}
```

## Execution Flow

```
Context → [on?] → handle → Next
         ↓ (false) → Skip
         
         ↓ (exception) → except → Next or Fail
```

## Error Handling

The `except` block handles exceptions and can perform compensation:

```kotlin
worker {
    handle { database.save(data) }
    except { e: Throwable ->
        // Compensation: rollback changes
        database.rollback()
        status = "ERROR"
    }
}
```