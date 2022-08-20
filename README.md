[![Download](https://img.shields.io/maven-central/v/com.crowdproj/kotlin-cor)](https://search.maven.org/artifact/com.crowdproj/kotlin-cor)

# Kotlin Chain of Responsibility Template Library

## Installation

#### **`build.gradle.kts`**
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    val kotlinCorVersion: String by project
    implementation("com.crowdproj:kotlin-cor:$kotlinCorVersion")
}
```
#### **`gradle.properties`**
```properties
kotlinCorVersion=0.5.4+
```

## Usage

First, build a business chain
```kotlin
val chain = rootChain<TestContext> {
    worker {
        title = "Status initialization"
        description = "Check the status initialization at the buziness chain start"

        on { status == CorStatuses.NONE }
        handle { status = CorStatuses.RUNNING }
        except { status = CorStatuses.FAILING }
    }

    chain {
        on { status == CorStatuses.RUNNING }

        worker(
            title = "Lambda worker",
            description = "Example of a buziness chain worker in a lambda form"
        ) {
            some += 4
        }
    }

    parallel {
        on {
            some < 15
        }

        worker(title = "Increment some") {
            some++
        }
    }
    printResult()

}.build()
```

Then start it with you context:
```kotlin
val ctx = TestContext(some = 13)
runBlocking { chain.exec(ctx) }
```
