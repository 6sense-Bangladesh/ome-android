import org.gradle.api.JavaVersion

@Suppress("ConstPropertyName")
object ProjectConfig {
    const val packageName = "com.ome.app"
    const val minSdk = 21
    const val compileSdk = 35
    const val targetSdk = 35

    const val versionCode = 1
    const val versionName = "1.0.012-ome-dev"

    val javaVersion = JavaVersion.VERSION_21

    const val BASE_URL_DEV = "\"https://app-dev.api.omekitchen.com\""
    const val BASE_URL_SANDBOX = "\"https://app-sandbox.api.omekitchen.com\""
    const val BASE_URL_LIVE = "\"https://app.api.omekitchen.com\""

    const val BASE_WS_URL_DEV = "\"wss://app-ws-dev.api.omekitchen.com\""
    const val BASE_WS_URL_SANDBOX = "\"wss://app-ws-sandbox.api.omekitchen.com\""
    const val BASE_WS_URL_LIVE = "\"wss://app-ws.api.omekitchen.com\""
}