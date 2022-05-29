import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "nl.avwie.common"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KotlinXCoroutinesCore)
                implementation(Dependencies.KotlinXSerializationCore)
                implementation(Dependencies.KotlinXSerializationJson)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Dependencies.KotlinXCoroutinesTest)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm(Dependencies.NpmUUID, Versions.NpmUUID))
            }
        }
    }
}