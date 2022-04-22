plugins {
    kotlin("multiplatform")
    //kotlin("plugin.serialization")
}

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
        val commonMain by getting
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