pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.6.20"
    }
}

rootProject.name = "scribbles"
include("dom")
include("vdom")
include("web-workers")

include("examples:dom-demo")
include("examples:vdom-demo")
