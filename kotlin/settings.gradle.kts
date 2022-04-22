pluginManagement {
    plugins {
        kotlin("multiplatform") version "1.6.21" apply false
        kotlin("js") version "1.6.21" apply false
        kotlin("plugin.serialization") version "1.6.21" apply false
    }
}

rootProject.name = "scribbles"
include("dom")
include("vdom")
include("web-workers")

include("examples:dom-demo")
include("examples:vdom-demo")
