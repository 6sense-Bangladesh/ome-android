plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = ProjectConfig.packageName
    compileSdk = ProjectConfig.compileSdk

    signingConfigs {
        create("release") {
            storeFile = file("ome.jks")
            storePassword = "ome-eH6MiV"
            keyAlias = "ome"
            keyPassword = "ome-eH6MiV"
        }
        getByName("debug") {
            initWith(signingConfigs.getByName("release"))
        }
        create("demo") {
            initWith(signingConfigs.getByName("release"))
        }
        create("sandbox") {
            initWith(signingConfigs.getByName("release"))
        }
    }

    defaultConfig {
        applicationId = ProjectConfig.packageName
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        ndk {
//            abiFilters.remove("riscv64")
//        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL_LIVE)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL_LIVE)
        }

        create("demo") {
            isDebuggable = true
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL_LIVE)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL_LIVE)
        }

        create("sandbox") {
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL_SANDBOX)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL_SANDBOX)
        }

        release {
            isMinifyEnabled = false
            isDebuggable = false
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL_LIVE)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL_LIVE)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    applicationVariants.all {
        outputs.forEach {
            val output = it as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "${rootProject.name.replace(' ', '_')}_v"
            output.outputFileName += "$versionName-$name.apk"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = ProjectConfig.javaVersion
        targetCompatibility = ProjectConfig.javaVersion
    }

    kotlin {
        jvmToolchain(ProjectConfig.javaVersion.toString().toInt())
    }

    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
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

//kapt {
//    correctErrorTypes = true
//}

dependencies {
    //noinspection GradleDependency
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

//    implementation("io.projectreactor.netty:reactor-netty-core:1.1.23")

    //DI
    implementation("com.google.dagger:hilt-android:2.52")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    ksp("com.google.dagger:hilt-compiler:2.52")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")

    //Rest API
    val retrofit2 = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit2")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    val cameraX = "1.5.0-alpha03"
    implementation("androidx.camera:camera-core:$cameraX")
    implementation("androidx.camera:camera-camera2:$cameraX")
    implementation("androidx.camera:camera-lifecycle:$cameraX")
    implementation("androidx.camera:camera-view:$cameraX")

    //QR code
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")
    implementation("androidx.camera:camera-mlkit-vision:$cameraX")

    //UI
    implementation("com.github.fornewid:neumorphism:0.3.2")
    implementation("com.google.android.material:material:1.12.0")
//    implementation("com.github.GrenderG:Toasty:1.5.2")

    // Amplify frameworks
    val amplify = "1.38.8" //Don't upgrade this version
    implementation("com.amplifyframework:core:$amplify")
    implementation("com.amplifyframework:aws-api:$amplify")
    implementation("com.amplifyframework:aws-auth-cognito:$amplify")
//    implementation("com.amplifyframework:aws-datastore:1.35.3")
    // Amplify coroutines
    //noinspection GradleDependency
    implementation("com.amplifyframework:core-kotlin:0.22.8") //Don't upgrade this version

    //Phone validator
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.49")

//    implementation("com.squareup.moshi:moshi:1.15.1")

    implementation("com.apachat:loadingbutton-android:1.0.11")

    //View
//    implementation("com.github.ramseth001:TextDrawable:1.1.6")

    implementation("com.jaredrummler:material-spinner:1.3.1")

    //image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")

    implementation("me.zhanghai.android.fastscroll:library:1.3.0")

    //Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    //wifi
    implementation("io.github.thanosfisherman.wifiutils:wifiutils:1.6.6")

    implementation("dev.chrisbanes.insetter:insetter:0.6.1")

//    implementation("com.google.guava:guava:33.3.1-android")

    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    val scarlet = "0.1.12"
    implementation("com.tinder.scarlet:scarlet:$scarlet")
    implementation("com.tinder.scarlet:stream-adapter-coroutines:$scarlet")
    implementation("com.tinder.scarlet:message-adapter-gson:$scarlet")
    implementation("com.tinder.scarlet:websocket-okhttp:$scarlet")

    implementation("com.neovisionaries:nv-websocket-client:2.14")

//    implementation("com.github.santalu:maskara:1.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.github.chuckerteam.chucker:library:4.0.0")
//    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    implementation("com.github.chesire:lifecyklelog:3.1.1")

    implementation("io.coil-kt:coil:2.7.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("com.airbnb.android:lottie:6.6.0")

    // Play In-App Update:
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

}
