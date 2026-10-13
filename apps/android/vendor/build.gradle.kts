plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id ("com.android.library")
    id ("kotlin-android")
}

android {
    compileSdk = Versions.Sdk.Compile

    defaultConfig {
        minSdk = Versions.Sdk.Min
    }

    buildFeatures {
        compose = true
    }
    namespace = "com.smarttoolfactory.speechbubble"
}

dependencies {
    implementation(Dependencies.ComposeX.Graphics)
    implementation(Dependencies.ComposeX.Material)
    implementation(Dependencies.ComposeX.Foundation)
}
