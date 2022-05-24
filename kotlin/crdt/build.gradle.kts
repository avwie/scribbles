plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                implementation(Dependencies.KotlinXSerializationJson)
                implementation(Dependencies.KotlinXDateTime)
                implementation(Dependencies.KotlinXImmutableCollections)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}