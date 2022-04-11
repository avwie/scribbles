pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.6.20"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


rootProject.name = "nl.avwie"
include("mvu:core")
include("dom:core")
include("dom:examples")
