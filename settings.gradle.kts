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
    "crutch_core",
    "crutch_states",
    "crutch_cacheable",
    "crutch_pager",
    "crutch_stringresources",
)
