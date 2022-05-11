plugins {
    kotlin("multiplatform")
}

group = "nl.avwie.common"
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm(Dependencies.NpmUUID, Versions.NpmUUID))
            }
        }
    }
}