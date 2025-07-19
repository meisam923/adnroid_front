plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // REMOVE the kotlin.compose plugin from here

    // ADD Hilt and Kapt plugins
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

android {
    namespace = "com.example.apfront"
    compileSdk = 36 // Make sure this is 34 as of the latest stable libraries

    defaultConfig {
        minSdk=26
    }

    buildTypes {
        // ... your existing config
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // It's safer to use 1.8 for wider compatibility
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    // ADD this block to specify the Compose Compiler version
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose ---
    // 1. Declare the BOM first to manage all Compose library versions
    implementation(platform(libs.androidx.compose.bom))

    // 2. Declare the individual Compose libraries (without versions)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Hilt for Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit for Networking
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Using string as it wasn't in your libs file

    // Coil for Image Loading
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}

// Add this block at the end of the file for Hilt
kapt {
    correctErrorTypes = true
}