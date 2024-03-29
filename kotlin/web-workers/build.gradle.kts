plugins {
    kotlin("plugin.serialization")
    kotlin("multiplatform")
}

group = "nl.avwie.web-workers"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js("worker", IR) {
        binaries.executable()
        browser {
            webpackTask {
                outputFileName = "worker.js"
            }
            distribution {
                name = "worker"
            }
        }
    }

    js("frontend", IR) {
        binaries.executable()
        browser {
            runTask {
                outputFileName = "frontend.js"
            }

            webpackTask {
                outputFileName = "frontend.js"
            }

            distribution {
                name = "frontend"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.KotlinXSerializationJson)
                implementation(Dependencies.KotlinXCoroutinesCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val workerMain by getting {

        }

        val frontendMain by getting {
            resources.srcDirs("./build/worker")
        }
    }
}

tasks["frontendProcessResources"].dependsOn.add("workerBrowserDistribution")