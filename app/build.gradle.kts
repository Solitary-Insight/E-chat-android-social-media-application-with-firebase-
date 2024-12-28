plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}
allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter() // Ensure this is here if needed
    }
}
android {
    namespace = "com.example.social_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.social_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = 1.5.0
//    }
    buildFeatures {
        compose =true
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

    implementation ("androidx.compose.material3:material3:1.3.1")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.google.android.material:material:1.6.0")
    implementation ("com.cloudinary:cloudinary-android:3.0.2")
    implementation ("com.cloudinary:cloudinary-android-download:3.0.2")

    implementation ("com.cloudinary:cloudinary-android-preprocess:3.0.2")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.auth)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.play.services.auth)
    implementation(libs.glide)
    implementation(libs.lottie)
    implementation(libs.lottie)
    implementation(libs.material3)
    implementation(libs.glide)




}