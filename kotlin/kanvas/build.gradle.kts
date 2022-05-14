plugins {
    kotlin("multiplatform")
}

group = "nl.avwie.kanvas"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {

    js(IR) {
        browser()
        binaries.executable()
    }

    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))
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