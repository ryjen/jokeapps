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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(Dependencies.ComposeX.Graphics)
    implementation(Dependencies.ComposeX.Material)
    implementation(Dependencies.ComposeX.Foundation)
}
