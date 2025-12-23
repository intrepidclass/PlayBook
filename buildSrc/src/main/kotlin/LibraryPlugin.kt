import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure // Required for the configure<T> syntax

class LibraryPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.pluginManager.apply("com.android.library")
    target.pluginManager.apply("kotlin-android")
    target.pluginManager.apply("voice.ktlint")

    // Access the version catalog
    val libs = target.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
    val minSdkVer = libs.findVersion("sdk-min").get().requiredVersion.toInt()
    val compileSdkVer = libs.findVersion("sdk-compile").get().requiredVersion.toInt()

    // Configure the LibraryExtension. This block will execute once the extension is available.
    target.extensions.configure<LibraryExtension> {
      namespace = "voice." + target.path.removePrefix(":").replace(':', '.')
      this.compileSdk = compileSdkVer // Use this.compileSdk to be explicit about the receiver

      defaultConfig {
        this.minSdk = minSdkVer // Use this.minSdk
        // targetSdk will typically default based on compileSdk or can be set explicitly if needed
      }
    }

    // The rest of your setup, which might re-apply or further configure these settings
    target.pluginManager.withPlugin("com.android.library") {
      target.baseSetup() // baseSetup() will re-apply/override some settings, which is fine.
      target.tasks.register("voiceUnitTest") {
        dependsOn("testDebugUnitTest")
      }
    }
  }
}
