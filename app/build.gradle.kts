plugins {
    id("com.android.application")
}

android {
    namespace = "vn.edu.usth.rssreader"

    compileSdk = 36

    defaultConfig {
        applicationId = "vn.edu.usth.rssreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
