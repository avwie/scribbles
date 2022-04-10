pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.6.20"
    }
}

rootProject.name = "nl.avwie.kotlin"
include("dom:core")
include("dom:examples")
