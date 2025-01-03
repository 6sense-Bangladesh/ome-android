plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
        buildConfigField("boolean", "IS_INTERNAL_TESTING", "Boolean.parseBoolean(\"${ProjectConfig.IS_INTERNAL_TESTING}\")")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL)
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
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", ProjectConfig.BASE_URL)
            buildConfigField("String", "BASE_WEB_SOCKET_URL", ProjectConfig.BASE_WS_URL)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
            pickFirsts += setOf("META-INF/INDEX.LIST", "META-INF/io.netty.versions.properties", "META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        }
    }
}

dependencies {
    //noinspection GradleDependency
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.2.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    //DI
    implementation("com.google.dagger:hilt-android:2.54")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    ksp("com.google.dagger:hilt-compiler:2.54")

    //Navigation
    val nav = "2.8.5"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav")
    implementation("androidx.navigation:navigation-ui-ktx:$nav")

    //Rest API
    val retrofit2 = "2.11.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit2")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    val cameraX = "1.5.0-alpha04"
    implementation("androidx.camera:camera-core:$cameraX")
    implementation("androidx.camera:camera-camera2:$cameraX")
    implementation("androidx.camera:camera-lifecycle:$cameraX")
    implementation("androidx.camera:camera-view:$cameraX")

    //QR code
    implementation("androidx.camera:camera-mlkit-vision:$cameraX")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")

    //UI
    implementation("com.google.android.material:material:1.12.0")

    // Amplify frameworks
    val amplify = "1.38.8" //Don't upgrade this version
    implementation("com.amplifyframework:core:$amplify")
    implementation("com.amplifyframework:aws-api:$amplify")
    implementation("com.amplifyframework:aws-auth-cognito:$amplify")
    // Amplify coroutines
    //noinspection GradleDependency
    implementation("com.amplifyframework:core-kotlin:0.22.8") //Don't upgrade this version
    implementation("com.amazonaws:aws-android-sdk-pinpoint:2.77.1")
    implementation("com.apachat:loadingbutton-android:1.0.11")

    //image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:ksp:4.16.0")

    //Splash screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    //socket data encryption
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Websocket Ktor
    val ktorVersion = "3.0.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

    //google truth

    val truth = "1.4.4"
    testImplementation("com.google.truth:truth:$truth")
    androidTestImplementation("com.google.truth:truth:$truth")

    if(ProjectConfig.IS_INTERNAL_TESTING)
        implementation("com.github.chuckerteam.chucker:library:4.0.0")
    else
        implementation("com.github.chuckerteam.chucker:library-no-op:4.0.0")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-messaging:24.1.0")

    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    implementation("com.github.chesire:lifecyklelog:3.1.1")

    val coil = "3.0.4"
    implementation("io.coil-kt.coil3:coil:$coil")
    implementation("io.coil-kt.coil3:coil-network-okhttp:$coil")
    implementation("io.coil-kt.coil3:coil-gif:$coil")

    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.browser:browser:1.8.0")

    // Play In-App Update:
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")


}
