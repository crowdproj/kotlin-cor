[![Download](https://img.shields.io/maven-central/v/com.crowdproj/kotlin-cor)](https://search.maven.org/artifact/com.crowdproj/kotlin-cor)

# Kotlin Chain of Responsibility Design Pattern Library

## Description

This is a Chain of Responsibility (CoR) Design Pattern Library. Its goal is to make business logics as simple as ...:

```kotlin
val bizLogics = rootChain<BizContext> {
    initialize("Initialization of the chain")
    chooseRepo("Choose test of prod repository")
    validation("Validation of the request") {
        validateIdNotEmpty("Validate the id to be not empty")
        validateIdFormat("Validate the id to have proper format")
        finishValidateion("Prepare response on errors")
    }
    readObject("Reading requested object")
    access("Check access rights") {
        accessRelations("Compute relationships of the requester to the object")
        accessCorPermissions("Compute permissions of the user to the object")
        accessValidate("Check the requested operation is permitted")
        accessFrontedPeremissions("Compute the user permissions to deliver to frontend")
    }
    response("Prepare response")
}.build()
val ctx = BizContext(
    idRequested = "<Object id from request obtained in controller>"
)
bizLogics.exec(ctx)
assertEquals(expected, ctx.objResponse)
```

Such a representation of the business logics has the following advantages.

1. It is optimized for human readability. So, any developer will easily find the required operation.
2. It is extremely agile and allows easily change the business process without substantial refactoring.
3. Provides "code first" approach that is better suit the needs of developers.

### CoR vs BPMS

BPMS engines provide a "declaration first" approach where business logics is developed in a visual designer. This is
may be very convenient for analysts, architects or manager but brings few disadvantages to developers. The main problem
is current engines use a schema: Visual Editor -> xml spec -> code.

1. This means that developers do not control the code. Any change by an analyst to BPM schema may break your application
   and bring a headache to the developer.
2. Autogenerated XML file is also severe and its manual change is problematic.
3. This prevents parallel development of the business processes since git-conflicts cannot be easily resolved.

This CoR library doesn't compete with BPM as is. But it allows developers to control the code themselves.

Compatibility between BPM and CoR is planned.

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
