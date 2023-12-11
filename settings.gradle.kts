pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = ("crutchLib")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "app",
    "crutch",
    "crutch-states"
)
