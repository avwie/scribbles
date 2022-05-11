plugins {
    kotlin("multiplatform") version Versions.KotlinMultiPlatform apply false
    kotlin("js") version Versions.KotlinMultiPlatform apply false
    kotlin("plugin.serialization") version Versions.KotlinMultiPlatform apply false
    id("org.jetbrains.compose") version Versions.JetbrainsCompose apply false
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

group = "nl.avwie"
version = "1.0-SNAPSHOT"