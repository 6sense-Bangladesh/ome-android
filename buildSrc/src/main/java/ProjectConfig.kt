import org.gradle.api.JavaVersion

@Suppress("ConstPropertyName", "MemberVisibilityCanBePrivate")
object ProjectConfig {
    const val packageName = "com.ome.app"
    const val minSdk = 21
    const val compileSdk = 35
    const val targetSdk = 35

    const val versionCode = 6
    const val versionName = "1.0.019-ome-dev"

    val javaVersion = JavaVersion.VERSION_21

    var IS_INTERNAL_TESTING = false

    const val BASE_URL_DEV = "\"https://app-dev.api.omekitchen.com\""
    const val BASE_URL_SANDBOX = "\"https://app-sandbox.api.omekitchen.com\""
    const val BASE_URL_LIVE = "\"https://app.api.omekitchen.com\""
    val BASE_URL_DEBUG = if(IS_INTERNAL_TESTING) BASE_URL_DEV else BASE_URL_LIVE

    const val BASE_WS_URL_DEV = "\"wss://app-ws-dev.api.omekitchen.com\""
    const val BASE_WS_URL_SANDBOX = "\"wss://app-ws-sandbox.api.omekitchen.com\""
    const val BASE_WS_URL_LIVE = "\"wss://app-ws.api.omekitchen.com\""
    val BASE_WS_URL_DEBUG = if(IS_INTERNAL_TESTING) BASE_WS_URL_DEV else BASE_WS_URL_LIVE
}