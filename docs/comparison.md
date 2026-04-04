# Comparison with Alternatives

## Overview

kotlin-cor targets **business logic orchestration** — sequential processing of requests within a single process. This is distinct from:

- **State machines** — transitions between discrete states
- **Workflow engines** — complex orchestration with durability
- **Saga pattern** — distributed transactions with compensation

## Comparison Table

| Library | Stars | Type | KMP | Durability | Retry/Comp | Use Case |
|---------|-------|------|-----|------------|------------|----------|
| **kotlin-cor** | 10 | Library | ✅ | ❌ | ✅ | Business logic pipelines |
| **Tinder/StateMachine** | 2071 | Library | ✅ | ❌ | ❌ | Finite state machines |
| **KStateMachine** | 486 | Library | ✅ | ❌ | ❌ | Hierarchical statecharts |
| **Square Workflow** | 1109 | Library | ✅ (JVM,iOS) | ❌ | ❌ | UI-driven workflows |
| **FlowRedux** | 776 | Library | ✅ | ❌ | ❌ | State machines on Flow |
| **Infinitic** | 358 | Platform | ❌ | ✅ | ✅ | Durable orchestration |
| **Arrow Saga** | 68 | Library | ✅ | ❌ | ✅ | Distributed transactions |
| **kfsm** | 32 | Library | ❌ | ❌ | ❌ | Type-safe FSM |

## Detailed Comparison

### kotlin-cor vs State Machines

**State machines** (Tinder/StateMachine, KStateMachine) are for:
- UI state management
- Protocol parsers
- Game logic
- Discrete state transitions

**kotlin-cor** is for:
- Sequential business processes
- Request pipelines
- Multi-step operations

```kotlin
// State Machine
stateMachine {
    state("idle")
    state("loading")
    state("success")
    
    transition("idle" -> "loading" on Load)
    transition("loading" -> "success" on DataLoaded)
}

// kotlin-cor - Business Pipeline
rootChain<Context> {
    worker { title = "Validate" }
    worker { title = "Authorize" }
    worker { title = "Process" }
}
```

### kotlin-cor vs Workflow Engines (Temporal, Infinitic)

**Workflow engines** require:
- External server/infrastructure
- Database for state persistence
- Heavy runtime

**kotlin-cor** is:
- Just a library
- In-process execution
- No infrastructure needed

| Aspect | Temporal/Infinitic | kotlin-cor |
|--------|-------------------|------------|
| Durability | ✅ Save to DB | ❌ In-memory |
| Infrastructure | Required | None |
| Recovery after crash | ✅ | ❌ |
| Complexity | High | Low |
| Use case | Hours-long processes | Seconds/minutes |

### kotlin-cor vs Saga (Arrow Saga)

**Arrow Saga** focuses on:
- Distributed transactions
- Rollback on failure
- Service-to-service calls

**kotlin-cor** focuses on:
- Sequential processing
- Error handling per step
- Conditional execution

```kotlin
// Arrow Saga - distributed compensation
saga {
    bind { orderService.create(order) }
    bind { paymentService.charge(customer, amount) }
    bind { inventoryService.reserve(items) }
}.onFailure { compensate() }

// kotlin-cor - sequential pipeline
chain {
    worker { handle { orderService.create(order) } }
    worker { handle { paymentService.charge(...) } }
    worker { handle { inventoryService.reserve(...) } }
    except { e -> rollback() }
}
```

### kotlin-cor vs Square Workflow

**Square Workflow** requires:
- UI integration (Compose/SwiftUI)
- Complex state/output model
- Subworkflow composition

**kotlin-cor** is:
- Backend-focused
- Simple context model
- Direct sequential execution

## When to Use What

| Scenario | Recommendation |
|----------|----------------|
| Request validation → DB fetch → Response | **kotlin-cor** |
| UI state management | KStateMachine, Tinder/StateMachine |
| Long-running with crash recovery | Temporal, Infinitic |
| Distributed transactions | Arrow Saga |
| Complex UI workflows | Square Workflow |

## Competitive Advantages of kotlin-cor

1. **KMP** — Works on JVM, JS, Native, Wasm
2. **Lightweight** — No dependencies beyond coroutines
3. **Simple** — No complex concepts to learn
4. **Readable** — Business logic reads like documentation
5. **Extensible** — Easy to add custom workers

## Related Libraries

- **kotlin-cor** — Business logic CoR (this project)
- **crowdproj/kotlin-cor-web** — HTTP request handling
- **crowdproj/kotlin-cor-auth** — Authentication chains

## See Also

- [Core Concepts](concepts.md)
- [Examples](examples.md)
- [DSL Reference](dsl.md)