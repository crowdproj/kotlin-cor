plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("signing")
    id("org.jetbrains.dokka")
}

group = "com.crowdproj"
version = "0.5.3"

repositories {
    mavenCentral()
}

signing {
    sign(publishing.publications)
}

kotlin {
    js(BOTH) {
        browser()
        nodejs()
    }
    jvm()
    linuxX64()
//    linuxArm64()
//    linuxArm32Hfp()
//    linuxMips32()
//    linuxMipsel32()
    ios()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    tvos()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchos()
    watchosArm32()
    watchosSimulatorArm64()
    watchosArm64()
    watchosX86()
    watchosX64()
//    wasm()
//    wasm32()
//    mingwX86()
    mingwX64()

    sourceSets {
        val coroutinesVersion: String by project

        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        @Suppress("UNUSED_VARIABLE")
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val linuxX64Main by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        @Suppress("UNUSED_VARIABLE")
        val linuxX64Test by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    group = "publishing"
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
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
        create<MavenPublication>("maven") {
            artifactId = project.name
            groupId = rootProject.group.toString()
            version = rootProject.version.toString()
            artifact(javadocJar)
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
    create("deploy") {
        group = "build"
        dependsOn(build)
        dependsOn(publish)
    }

}
