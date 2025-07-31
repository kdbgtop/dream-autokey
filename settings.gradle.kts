pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "dream-autokey"

include(":plugin-core")
include(":plugin-core:nms:api")