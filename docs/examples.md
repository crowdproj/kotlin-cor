# Examples

## Simple: Hello World

```kotlin
data class HelloContext(var message: String = "")

val chain = rootChain<HelloContext> {
    worker {
        title = "Say Hello"
        handle { message = "Hello, World!" }
    }
}.build()

runBlocking { 
    val ctx = HelloContext()
    chain.exec(ctx)
    println(ctx.message) // "Hello, World!"
}
```

## Intermediate: Request Validation

```kotlin
data class RequestContext(
    var status: String = "RECEIVED",
    var requestId: String = "",
    var data: Map<String, String> = emptyMap(),
    var errors: List<String> = emptyList(),
    var validated: Boolean = false
)

val validateChain = rootChain<RequestContext> {
    worker(title = "Check ID") {
        on { requestId.isNotBlank() }
        handle { validated = true }
        except { errors += "Invalid ID" }
    }
    
    worker(title = "Check data") {
        on { validated }
        handle { 
            require(data.isNotEmpty()) { "Empty data" }
        }
        except { errors += "Data validation failed" }
    }
}.build()
```

## Advanced: Multi-Step Business Process

```kotlin
data class UserContext(
    var userId: String = "",
    var status: String = "INIT",
    var user: User? = null,
    var permissions: List<String> = emptyList(),
    var data: Map<String, Any> = emptyMap(),
    var response: HttpResponse? = null
)

data class UserSettings(
    val userService: UserService,
    val permissionService: PermissionService,
    val logger: Logger
)

val userChain = rootChain<UserContext, UserSettings>(settings) {
    // Step 1: Fetch user
    worker(title = "Fetch user") {
        on { userId.isNotBlank() }
        handle { 
            user = config.userService.getById(userId)
        }
        except { e ->
            status = "ERROR"
            config.logger.error("Failed to fetch user", e)
        }
    }
    
    // Step 2: Get permissions in parallel
    parallel(title = "Load permissions") {
        on { user != null }
        
        worker(title = "Role permissions") {
            handle { 
                permissions += config.permissionService.getRoles(userId)
            }
        }
        
        worker(title = "Custom permissions") {
            handle { 
                permissions += config.permissionService.getCustom(userId)
            }
        }
    }
    
    // Step 3: Process with retry
    loopWhile(title = "Process data") {
        on { user != null && status != "DONE" }
        
        check { status != "PROCESSING" }
        
        worker(title = "Process") {
            handle {
                status = "PROCESSING"
                data = config.userService.process(userId, permissions)
                status = "DONE"
            }
            except { 
                status = "RETRY"
            }
        }
    }
    
    // Step 4: Build response
    worker(title = "Build response") {
        on { status == "DONE" }
        handle {
            response = HttpResponse(
                status = 200,
                body = UserResponse(user, permissions, data)
            )
        }
    }
}.build()

// Execution
runBlocking {
    val ctx = UserContext(userId = "123")
    userChain.exec(ctx)
    println(ctx.response)
}
```

## Advanced: Conditional Branching

```kotlin
val processChain = rootChain<ProcessContext> {
    worker(title = "Detect type") {
        handle { 
            type = detectType(input)
        }
    }
    
    // Conditional execution using on{}
    worker(title = "Handle A") {
        on { type == Type.A }
        handle { result = processA(input) }
    }
    
    worker(title = "Handle B") {
        on { type == Type.B }
        handle { result = processB(input) }
    }
    
    worker(title = "Handle default") {
        on { type == Type.DEFAULT }
        handle { result = processDefault(input) }
    }
    
    worker(title = "Finalize") {
        on { result != null }
        handle { status = "COMPLETED" }
    }
}
```

## Testing

Each worker is a testable unit:

```kotlin
class WorkerTest {
    @Test
    fun `worker executes on condition`() = runTest {
        val context = TestContext(status = "RUNNING")
        
        val worker = CorWorkerDsl<TestContext, Unit>(Unit).apply {
            title = "Test"
            on { status == "RUNNING" }
            handle { data = "processed" }
        }.build()
        
        worker.exec(context)
        
        assertEquals("processed", context.data)
    }
    
    @Test
    fun `worker skips when condition false`() = runTest {
        val context = TestContext(status = "STOPPED")
        
        val worker = CorWorkerDsl<TestContext, Unit>(Unit).apply {
            title = "Test"
            on { status == "RUNNING" }
            handle { data = "processed" }
        }.build()
        
        worker.exec(context)
        
        assertNull(context.data) // Not executed
    }
}
```

## Extension Functions

Create reusable business logic as extensions:

```kotlin
fun ICorDslAdd<BizContext, Settings>.validateId() = worker {
    title = "Validate ID"
    on { id.isNotBlank() }
    handle { 
        require(id.matches(idRegex)) { "Invalid ID format" }
    }
}

fun ICorDslAdd<BizContext, Settings>.fetchEntity() = worker {
    title = "Fetch entity"
    on { id.isNotBlank() }
    handle { 
        entity = config.repository.findById(id)
    }
    except { 
        status = "ERROR" 
    }
}

// Usage
val chain = rootChain<BizContext, Settings>(settings) {
    validateId()
    fetchEntity()
    respond()
}.build()
```