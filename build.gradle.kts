plugins {
    kotlin("multiplatform")
    `maven-publish`
    id("org.jetbrains.dokka")
}

group = "com.crowdproj.kotlin.cor"
version = "0.2.4"

repositories {
    mavenCentral()
}

kotlin {
    js {
//        browser {}
        nodejs()
    }
    jvm {

    }

    sourceSets {
        val coroutinesVersion: String by project

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
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
        maven {
            name = "GitHubPackages"
            url = uri(System.getenv("NEXUS_HOST") ?: "https://maven.pkg.github.com/crowdproj/kotlin-cor")
            credentials {
                username = System.getenv("NEXUS_USER") ?: System.getenv("GITHUB_ACTOR")
                password = System.getenv("NEXUS_PASS") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(javadocJar)
    }
}
