# DSL Reference

## Entry Points

### `rootChain`

Creates the root of a chain of responsibility:

```kotlin
// Without settings
val chain = rootChain<Context> { ... }.build()

// With settings
val chain = rootChain<Context, Settings>(settings) { ... }.build()
```

### `rootWorker`

Creates a standalone worker:

```kotlin
val worker = rootWorker<Context> { ... }.build()
val worker = rootWorker<Context, Settings>(settings) { ... }.build()
```

## DSL Builders

### Worker DSL

```kotlin
worker { ... }
worker(title = "Name") { ... }
worker(title = "Name", description = "Desc") { ... }
worker(title = "Name", description = "Desc") {
    on { condition }
    handle { /* code */ }
    except { e: Throwable -> /* handle error */ }
}
```

Properties:
- `title: String` — worker name
- `description: String` — documentation

Methods:
- `on { condition }` — execution condition
- `handle { code }` — execution logic
- `except { e: Throwable -> code }` — error handler

### Chain DSL

```kotlin
chain {
    title = "Chain name"
    description = "Description"
    on { condition }
    except { e: Throwable -> code }
    
    worker { ... }
    worker { ... }
    chain { ... }
}
```

### Parallel DSL

```kotlin
parallel {
    title = "Parallel section"
    on { condition }
    
    worker { ... }
    worker { ... }
}
```

### Loop DSL

```kotlin
loopWhile {
    check { condition }
    worker { ... }
}

loopUntil {
    check { condition }
    worker { ... }
}
```

## Context Access

Inside workers, access context properties directly:

```kotlin
worker {
    handle {
        // Read
        val value = input
        
        // Write
        output = value.uppercase()
        
        // Modify
        items.add(newItem)
    }
}
```

## Settings Access

Access configuration via `config`:

```kotlin
worker {
    handle {
        val db = config.database
        val logger = config.logger
        db.save(data)
        logger.info("Saved")
    }
}
```

## Type Aliases

```kotlin
typealias ICorDslAdd<T,C> = ICorExecDsl<T,C>
typealias CorChainDsl<T,C> = BaseCorChainDsl<T,T,C>
```

## Building and Execution

```kotlin
// Build the chain
val exec: ICorExec<Context> = chain.build()

// Execute synchronously
exec.exec(context)

// Execute in coroutine scope
runBlocking { chain.exec(context) }
```

## Full Example

```kotlin
data class OrderContext(
    var status: String = "NEW",
    var orderId: String = "",
    var items: List<Item> = emptyList(),
    var validated: Boolean = false,
    var authorized: Boolean = false,
    var result: OrderResult? = null
)

val settings = OrderSettings(
    validator = OrderValidator(),
    permissions = PermissionsService()
)

val orderChain = rootChain<OrderContext, OrderSettings>(settings) {
    // Step 1: Validation
    chain {
        title = "Order validation"
        on { status == "NEW" }
        
        worker(title = "Check ID") {
            on { orderId.isNotBlank() }
            handle { validated = true }
        }
        
        worker(title = "Validate items") {
            on { validated && items.isNotEmpty() }
            handle {
                items.forEach { require(it.isValid()) }
            }
        }
    }
    
    // Step 2: Authorization
    parallel {
        title = "Authorization"
        on { validated }
        
        worker(title = "Check user") {
            handle { authorized = config.permissions.canRead(orderId) }
        }
        
        worker(title = "Check scope") {
            handle { /* scope check */ }
        }
    }
    
    // Step 3: Processing with retry
    loopUntil {
        title = "Process order"
        on { authorized && !completed }
        
        check { attempts < 3 }
        
        worker(title = "Process") {
            handle { result = processOrder(orderId) }
        }
    }
    
}.build()

// Run
runBlocking { orderChain.exec(OrderContext(...)) }
```