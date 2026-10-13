plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = Versions.Sdk.Compile

    defaultConfig {
        minSdk = Versions.Sdk.Min
    }
    namespace = "com.github.ryjen.jokeapp.meta"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(Dependencies.Koin.Android)
    implementation(Dependencies.CoroutinesX.Android)
    implementation(Dependencies.CoroutinesX.Core)

    testImplementation(Dependencies.JUnit)
    testImplementation(Dependencies.Truth)
}
