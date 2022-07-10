import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")
}

group = "nl.avwie.crdt.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {

    jvm {
        withJava()
    }

    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KotlinXSerializationCore)
                implementation(Dependencies.KotlinXCoroutinesCore)
                implementation(project(":common"))
                implementation(project(":crdt"))
                implementation(compose.runtime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(Dependencies.KtorServerCore)
                implementation(Dependencies.KtorServerNetty)
                implementation(Dependencies.KtorServerCallLogging)
                implementation(Dependencies.KtorServerCors)
                implementation(Dependencies.LogBackClassic)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
            }
        }
    }
}

val jvmJar by tasks.existing
val jvmRuntimeClasspath by configurations.existing

tasks.register<JavaExec>("runServer") {
    description = "Run the server"
    group = ApplicationPlugin.APPLICATION_GROUP
    classpath(jvmJar, jvmRuntimeClasspath)
    mainClass.set("nl.avwie.examples.crdt.ApplicationKt")
}