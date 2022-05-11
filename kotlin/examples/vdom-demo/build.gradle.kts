plugins {
    kotlin("multiplatform")
}

group = "nl.avwie.vdom.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {

    js("stress", IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":vdom"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}