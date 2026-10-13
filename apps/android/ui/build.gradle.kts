plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.Sdk.Compile

    defaultConfig {
        minSdk = Versions.Sdk.Min
    }

    buildFeatures {
        compose = true
    }

    namespace = "com.github.ryjen.jokeapp.ui"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":vendor"))
    implementation(Dependencies.AppCompat)
    implementation(Dependencies.ComposeX.Tooling)
    implementation(Dependencies.ComposeX.Foundation)
    implementation(Dependencies.ComposeX.Material)
    implementation(Dependencies.ComposeX.MaterialIcons)

    implementation(Dependencies.NavigationX.Compose)
    implementation(Dependencies.Koin.Android)
    implementation(Dependencies.Koin.Compat)
    implementation(Dependencies.Koin.Compose)

    implementation(Dependencies.Accompanist.Placeholder)
    implementation(Dependencies.Accompanist.Insets)
    implementation(Dependencies.Accompanist.SystemUI)

    implementation(Dependencies.Icons)
}
