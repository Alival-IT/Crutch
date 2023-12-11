plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    jacoco
    alias(libs.plugins.kover)
}

group = CrutchConfigStates.CRUTCH_GROUP_ID
version = CrutchConfigStates.CRUTCH_VERSION_NAME

android {
    compileSdk = libs.versions.compile.sdk.version.get().toInt()
    namespace = CrutchConfigStates.CRUTCH_LIB_ID

    defaultConfig {
        minSdk = libs.versions.min.sdk.version.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compilerextension.get()
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        disable.add("GradleDependency")
    }

    buildTypes {
        getByName("release") {
            enableUnitTestCoverage = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "consumer-rules.pro"
            )
            consumerProguardFiles("consumer-rules.pro")
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

jacoco {
    toolVersion = "0.8.9"
}

dependencies {
    implementation(projects.crutch)
    // Androidx
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.lifecycle)

    // Logging
    implementation(libs.timber)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.junit5.jupiter.api)
    testImplementation(libs.junit5.jupiter.params)
    testRuntimeOnly(libs.junit5.jupiter.engine)
    testRuntimeOnly(libs.junit5.vitage.engine)
    testImplementation(libs.coroutine.tests)
    testImplementation(libs.turbine)
}