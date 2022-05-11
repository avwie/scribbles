plugins {
    kotlin("multiplatform")
}

group = "nl.avwie.dom.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js("demo", IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":dom"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}