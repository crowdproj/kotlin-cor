rootProject.name = "kotlin-cor"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings

        kotlin("multiplatform") version kotlinVersion
        `maven-publish`
    }
}
