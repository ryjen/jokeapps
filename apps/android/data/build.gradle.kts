plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("app.cash.sqldelight")
}

sqldelight {
    databases {
        create("JokeDatabase") {
            packageName.set("com.github.ryjen.jokeapp.data.storage")
        }
    }
}

android {
    compileSdk = Versions.Sdk.Compile

    defaultConfig {
        minSdk = Versions.Sdk.Min
    }
    namespace = "com.github.ryjen.jokeapp.data"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    testImplementation(Dependencies.JUnit)

    implementation(project(":domain"))

    implementation(Dependencies.Koin.Android)

    implementation(Dependencies.SqlDelight.Android)
    implementation(Dependencies.SqlDelight.Coroutines)

    implementation(Dependencies.Ktor.Android)
    implementation(Dependencies.Ktor.Client)
    implementation(Dependencies.Ktor.Content)
    implementation(Dependencies.Ktor.Json)
    implementation(Dependencies.Ktor.Logging)
    implementation(Dependencies.Ktor.Serialize)
    implementation(Dependencies.Ktor.Logback)
    implementation(Dependencies.Serialization.Json)

    implementation(Dependencies.Timber)

    testImplementation(Dependencies.JUnit)
    testImplementation(Dependencies.Kotlin.Test)
    testImplementation(Dependencies.Ktor.Mock)
}
