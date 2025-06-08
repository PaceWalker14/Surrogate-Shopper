plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.surrogateshopper"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.surrogateshopper"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20230227") // For JSONObject & JSONException
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Required for Android 12+ splash screen support
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}



