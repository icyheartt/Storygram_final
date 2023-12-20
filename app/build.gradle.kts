plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.home33"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.home33"
        minSdk = 33
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildToolsVersion = "34.0.0"
    buildFeatures{
        dataBinding; true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    dataBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.greenfrvr:hashtag-view:1.3.0")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
