plugins {
    kotlin("multiplatform")
}

rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
}

group = "nl.avwie.dom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js("demo", IR) {
        browser()
        binaries.executable()
    }

    js("stress", IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":dom:core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}