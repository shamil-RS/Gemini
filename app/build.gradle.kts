import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists())
    localProperties.load(FileInputStream(localPropertiesFile))
else println("local.properties file not found!")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets)
    id("kotlinx-serialization")
    id("kotlin-parcelize")
}

kotlin {
    jvmToolchain(17)
}

secrets {
    defaultPropertiesFileName = "secret.defaults.properties"
}

android {
    namespace = "com.example.geminiai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.geminiai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        buildConfigField("String", "API_KEY", "\"${localProperties.getProperty("API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.camera.extensions)
    implementation(libs.profileinstaller)

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material)
    implementation(libs.navigation3.ui.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material)

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.text.google.fonts)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.adaptive)
    implementation(libs.compose.material.icons)
    androidTestImplementation(libs.compose.ui.test)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manisfest)

    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)

    implementation(libs.accompanist.painter)
    implementation(libs.accompanist.permissions)

    implementation(libs.graphics.shapes)

    implementation(libs.lifecycle.ktx)
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.runtime.compose)

    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    androidTestImplementation(libs.room.testing)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.splashscreen)
    implementation(libs.concurrent.kts)

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.window)

    androidTestImplementation(libs.turbine)

    implementation(libs.activity)

    implementation(libs.coil.compose.android)
    implementation(libs.generativeai)
    implementation(libs.datastore)

    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.core.testing)
    androidTestImplementation(libs.hilt.android.testing)
}
