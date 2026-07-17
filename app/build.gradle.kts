plugins {
    //alias(libs.plugins.android.application)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // new
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.objectbox)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.test2"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.test2"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // new
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.objectbox.kotlin)
    implementation(libs.objectbox.android)
    kapt(libs.objectbox.kotlin)
    testImplementation(libs.objectbox.testing.macos)
    testImplementation(libs.kotlinx.coroutines.test)

}

// Kotlin DSL (build.gradle.kts)
tasks.configureEach {
    if (name == "objectboxPrepareBuild") {
        notCompatibleWithConfigurationCache("ObjectBox Gradle plugin does not support the configuration cache.")
    }
}