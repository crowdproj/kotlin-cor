@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.plugin.dokka)
    `maven-publish`
    signing
}

group = "com.crowdproj"
version = "0.6.0"

repositories {
    mavenCentral()
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY_ID"),
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_PASSWORD")
    )
    sign(publishing.publications)
}

kotlin {
    js {
        browser()
        nodejs()
    }
    jvm()
    linuxX64()
    linuxArm64()
    iosX64()
    iosArm64()
//    iosSimulatorArm64()
    macosX64()
    macosArm64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosSimulatorArm64()
    watchosArm64()
    watchosX64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    mingwX64()

    sourceSets {
        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.atomicfu)
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)
val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
    from(dokkaHtml)
}

publishing {
    repositories {
        val repoHost: String = System.getenv("NEXUS_HOST") ?: "https://maven.pkg.github.com/crowdproj/kotlin-cor"
        val repoUser: String? = System.getenv("NEXUS_USER") ?: System.getenv("GITHUB_ACTOR")
        val repoPass: String? = System.getenv("NEXUS_PASS") ?: System.getenv("GITHUB_TOKEN")
        if (repoUser != null && repoPass != null) {
            maven {
                name = "GitHubPackages"
                url = uri(repoHost)
                credentials {
                    username = repoUser
                    password = repoPass
                }
            }
        }
    }
    publications {
        withType(MavenPublication::class).configureEach {
            artifact(javadocJar)
            pom {
                name.set("Kotlin CoR")
                description.set(
                    "Chain of Responsibility Design Template Library for human readable business " +
                            "logic: $name platform"
                )
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
    }
}

tasks {
    withType<Test> {
        reports {
            junitXml.required.set(true)
        }
        setupTestLogging()
    }

    publish {
        dependsOn(build)
    }
    create("deploy") {
        group = "build"
        dependsOn(publish)
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
                if (suite.parent != null) { // will match the outermost suite
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
