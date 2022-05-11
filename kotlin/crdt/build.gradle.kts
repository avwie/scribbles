plugins {
    kotlin("multiplatform")
}

group = "nl.avwie.crdt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    js(IR) {
        browser()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(Dependencies.KotlinXDateTime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}