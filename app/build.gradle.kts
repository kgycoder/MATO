plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.xware"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xware"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // NewPipeExtractor 의 multidex 지원
        multiDexEnabled = true
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

    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources {
            // NewPipeExtractor / Rhino 에서 발생하는 META-INF 충돌 제거
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/*.kotlin_module"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // NewPipeExtractor — NewPipe 앱이 사용하는 YouTube 스트림 추출 라이브러리
    // Rhino JS 엔진 내장 → nsig 복호화 서버 없이 자체 처리
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.24.2")
}
