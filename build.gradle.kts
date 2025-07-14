// In apFront/build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false

    // Add this line to define the kapt plugin for your project
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
}