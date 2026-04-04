# Contributing to kotlin-cor

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/crowdproj/kotlin-cor.git`
3. Run tests: `./gradlew check`

## Project Structure

```
src/
├── commonMain/kotlin/
│   ├── cor.kt                 # Core interfaces
│   ├── CorDslMarker.kt        # DSL marker annotation
│   ├── handlers/
│   │   ├── Worker.kt          # Worker implementation
│   │   ├── Chain.kt           # Chain implementation
│   │   ├── Parallel.kt        # Parallel execution
│   │   └── Loop.kt            # Loop implementation
│   └── base/
│       ├── BaseCorWorkerDsl.kt
│       ├── BaseCorChain.kt
│       └── BaseCorChainDsl.kt
└── commonTest/kotlin/
    ├── CorTest.kt
    └── helper/
```

## Adding New Handlers

1. Create handler class in `src/commonMain/kotlin/handlers/`
2. Implement `ICorWorker` or extend `BaseCorChain`
3. Create DSL class with `@CorDslMarker`
4. Add extension function on `ICorAddExecDsl`
5. Add tests in `src/commonTest/kotlin/`

Example:

```kotlin
// MyHandler.kt
class MyHandler<T>(...) : ICorWorker<T> { ... }

@CorDslMarker
class MyHandlerDsl<T, C>(config: C) : BaseCorWorkerDsl<T, C>(config) {
    override fun build(): ICorExec<T> = MyHandler(...)
}

fun <T, C> ICorAddExecDsl<T, C>.myHandler(function: MyHandlerDsl<T, C>.() -> Unit) {
    add(MyHandlerDsl(config).apply(function))
}
```

## Coding Standards

- Follow Kotlin conventions
- Use meaningful names for workers/chains
- Add `title` and `description` to all handlers
- Write tests for new functionality
- Update documentation

## Testing

```bash
# Run all tests
./gradlew check

# Run specific test
./gradlew jvmTest --tests "CorTest"
```

## Pull Request Process

1. Create a feature branch
2. Add tests for new functionality
3. Update documentation if needed
4. Ensure `./gradlew check` passes
5. Submit PR with description

## Code of Conduct

Be respectful and constructive. See [CODE_OF_CONDUCT.md](../CODE_OF_CONDUCT.md).