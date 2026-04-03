@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.plugin.dokka)
    alias(libs.plugins.plugin.dokka.javadoc)
    alias(libs.plugins.maven.publish)
}

group = "com.crowdproj"
version = "1.0.0"

repositories {
    mavenCentral()
}

dokka {
    pluginsConfiguration.html {
        footerMessage.set("(c) Sergey Okatov")
        separateInheritedMembers.set(false)
        mergeImplicitExpectActualDeclarations.set(false)
    }
}

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }
    jvm()
    linuxX64()
    linuxArm64()
    iosX64()
    iosArm64()
    macosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosArm32()
    watchosSimulatorArm64()
    watchosArm64()
    wasmJs {
        browser()
        nodejs()
    }
    wasmWasi {
        nodejs()
    }
    mingwX64()
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX64()
    androidNativeX86()

    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.atomicfu)
            }
        }

        jsTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        wasmJsTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        jvmTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "kotlin-cor", version.toString())
    pom {
        name.set("Kotlin CoR")
        description.set("Chain of Responsibility Design Template Library for human readable business logic")
        url.set("https://github.com/crowdproj/kotlin-cor")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                name.set("Sergey Okatov")
                email.set("sokatov@gmail.com")
                id.set("svok")
                organization.set("CrowdProj")
                organizationUrl.set("https://crowdproj.com")
                timezone.set("GMT+5")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/crowdproj/kotlin-cor.git")
            developerConnection.set("scm:git:ssh://github.com/crowdproj/kotlin-cor.git")
            url.set("https://github.com/crowdproj/kotlin-cor")
        }
    }
}

extra["mavenCentralUsername"] = System.getenv("NEXUS_USER")
extra["mavenCentralPassword"] = System.getenv("NEXUS_PASS")
extra["signingInMemoryKey"] = System.getenv("SIGNING_KEY")
extra["signingInMemoryKeyId"] = System.getenv("SIGNING_KEY_ID")
extra["signingInMemoryKeyPassword"] = System.getenv("SIGNING_PASSWORD")

tasks {
    withType<Test> {
        reports {
            junitXml.required.set(true)
        }
        setupTestLogging()
    }
}

fun Test.setupTestLogging() {
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
        )
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true

        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent != null) {
                    val output = "Results: ${result.resultType} (${result.testCount} tests, " +
                            "${result.successfulTestCount} passed, ${result.failedTestCount} failed, " +
                            "${result.skippedTestCount} skipped)"
                    val startItem = "|  "
                    val endItem = "  |"
                    val repeatLength = startItem.length + output.length + endItem.length
                    val messages = """
                        ${(1..repeatLength).joinToString("") { "-" }}
                        $startItem$output$endItem
                        ${(1..repeatLength).joinToString("") { "-" }}
                    """.trimIndent()
                    println(messages)
                }
            }
        })
    }
}
