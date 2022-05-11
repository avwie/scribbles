// Add maven repositories
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "nl.avwie.kanvas-compose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)
            }
        }
    }
}