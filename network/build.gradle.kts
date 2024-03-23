import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.serialization)
}

android {
    namespace = "dev.farukh.network"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "DaDataURL", readLocalProperties("daDataAPI"))
        buildConfigField("String", "DaDataSecret", readLocalProperties("daDataSecret"))
        buildConfigField("String", "DaDataApiKey", readLocalProperties("daDataApiKey"))
        buildConfigField("String", "CopyCloseURL", readLocalProperties("copyCloseUrl"))
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
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    //ktor-engine
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

fun readLocalProperties(key: String): String {
    val properties = Properties()
    val localProperties = File(rootDir, "local.properties")
    if (localProperties.isFile) {
        FileInputStream(localProperties).use { fis ->
            properties.load(fis)
        }
    } else {
        throw FileNotFoundException("local.properties file not found")
    }
    return properties.getProperty(key) ?: ""
}