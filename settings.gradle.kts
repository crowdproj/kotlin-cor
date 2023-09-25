rootProject.name = "kotlin-cor"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val dokkaVersion: String by settings
        val nexusStagingVersion: String by settings

        kotlin("multiplatform") version kotlinVersion
        `maven-publish`
        id("org.jetbrains.dokka") version dokkaVersion
        id("io.codearte.nexus-staging") version nexusStagingVersion
    }
}
