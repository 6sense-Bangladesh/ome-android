import org.gradle.api.JavaVersion

@Suppress("ConstPropertyName")
object ProjectConfig {
    const val packageName = "com.ome.app"
    const val minSdk = 21
    const val compileSdk = 35
    const val targetSdk = 35

    const val versionCode = 1
    const val versionName = "1.0.006-ome-dev"

    val javaVersion = JavaVersion.VERSION_21
}