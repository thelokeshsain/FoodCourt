plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Google Services Plugin
}

android {
    namespace = "com.example.foodcourt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodcourt"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Core libraries
    implementation(libs.appcompat) // AppCompat library
    implementation(libs.material) // Material Components
    implementation(libs.activity) // Activity library
    implementation(libs.constraintlayout) // Constraint Layout
    implementation(platform("com.google.firebase:firebase-bom:33.5.0")) // Firebase BOM for version management
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation(libs.firebase.auth) // Firebase Authentication
    implementation(libs.firebase.database) // Firebase Realtime Database
    implementation(libs.recyclerview) // RecyclerView for displaying lists
    implementation("androidx.recyclerview:recyclerview:1.3.0") // Specific RecyclerView version

    // Image loading (Glide)
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0") // For Glide's annotation processing

    // PDF generation with iText
    implementation("com.itextpdf:itextg:5.5.10")


    implementation("com.cashfree.pg:api:2.1.23")
    implementation("com.cashfree.pg:core:2.0.1")

    implementation ("com.google.code.gson:gson:2.8.8")



    // Firebase Storage
    implementation(libs.firebase.storage)

    // Unit testing dependencies
    testImplementation(libs.junit) // JUnit for unit tests
    androidTestImplementation(libs.ext.junit) // JUnit extensions for Android
    androidTestImplementation(libs.espresso.core) // Espresso for UI tests
}
