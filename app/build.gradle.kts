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
    // ... your dependencies remain the same
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Make sure to add Hilt dependencies here as well
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // And Retrofit, etc.
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")
}

// Add this block at the end of the file for Hilt
kapt {
    correctErrorTypes = true
}