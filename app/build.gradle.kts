@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.ome.Ome"
    compileSdk = 33

    signingConfigs {
        getByName("debug") {
            storeFile = file("ome.jks")
            storePassword = "ome-eH6MiV"
            keyAlias = "ome"
            keyPassword = "ome-eH6MiV"
        }
        create("release") {
            storeFile = file("ome.jks")
            storePassword = "ome-eH6MiV"
            keyAlias = "ome"
            keyPassword = "ome-eH6MiV"
        }

//      sandbox {
//          initWith signingConfigs.debug
//      }
//      demo {
//          initWith signingConfigs.debug
//      }

    }

    defaultConfig {
        applicationId = "com.ome.Ome"
        minSdk = 21
        targetSdk = 33
        versionCode = 6
        versionName = "1.06"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://app-dev.api.omekitchen.com/\"")
            buildConfigField("String", "BASE_WEB_SOCKET_URL", "\"wss://app-ws-dev.api.omekitchen.com\"")
        }

        create("sandbox") {
            buildConfigField("String", "BASE_URL", "\"https://app-sandbox.api.omekitchen.com/\"")
            buildConfigField("String", "BASE_WEB_SOCKET_URL", "\"wss://app-ws-sandbox.api.omekitchen.com\"")
        }

        create("demo") {
            buildConfigField("String", "BASE_URL", "\"https://app-dev.api.omekitchen.com/\"")
            buildConfigField("String", "BASE_WEB_SOCKET_URL", "\"wss://app-ws-dev.api.omekitchen.com\"")
        }


        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("String", "BASE_URL", "\"https://app.api.omekitchen.com/\"")
            buildConfigField("String", "BASE_WEB_SOCKET_URL", "\"wss://app-ws.api.omekitchen.com\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            signingConfig = signingConfigs.getByName("release")
//            signingConfig signingConfigs.release
        }
    }
    applicationVariants.all{
        outputs.forEach{
            val output=it as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName="${rootProject.name.replace(' ','_')}_v"
            output.outputFileName+="$versionName-$name.apk"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    packaging {
        resources {
            pickFirsts += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/io.netty.versions.properties"
            )
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("io.projectreactor.netty:reactor-netty-core:1.1.3")

    //DI
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    kapt("com.google.dagger:hilt-compiler:2.44")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    //Rest API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //QR code
    implementation("com.google.zxing:core:3.4.0")

    //UI
    implementation("com.github.fornewid:neumorphism:0.3.2")
    implementation("com.google.android.material:material:1.7.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")

    // Amplify frameworks
    implementation("com.amplifyframework:core:1.1.2")
    implementation("com.amplifyframework:aws-datastore:1.35.3")
    implementation("com.amplifyframework:aws-api:1.35.3")
    implementation("com.amplifyframework:aws-auth-cognito:1.35.3")

    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //Phone validator
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.3")

    // Amplify coroutines
    implementation("com.amplifyframework:core-kotlin:0.19.3")

    implementation("com.squareup.moshi:moshi:1.14.0")

    implementation("com.apachat:loadingbutton-android:1.0.11")

    //View
    implementation("com.github.ramseth001:TextDrawable:1.1.6")

    implementation("com.jaredrummler:material-spinner:1.3.1")

    //image loading
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    implementation("me.zhanghai.android.fastscroll:library:1.1.8")

    //Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.0")

    //wifi
    implementation("io.github.thanosfisherman.wifiutils:wifiutils:1.6.6")

    implementation("dev.chrisbanes.insetter:insetter:0.6.1")

    implementation("com.google.guava:guava:31.1-android")

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    implementation("com.tinder.scarlet:scarlet:0.1.12")
    implementation("com.tinder.scarlet:stream-adapter-coroutines:0.1.12")
    implementation("com.tinder.scarlet:message-adapter-gson:0.1.12")
    implementation("com.tinder.scarlet:websocket-okhttp:0.1.12")

    implementation("com.neovisionaries:nv-websocket-client:2.14")

    implementation("com.github.santalu:maskara:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    debugImplementation("com.github.chuckerteam.chucker:library:4.0.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")
}
