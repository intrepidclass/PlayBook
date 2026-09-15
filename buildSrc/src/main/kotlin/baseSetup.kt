import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.baseSetup() {
  val libs: VersionCatalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
  tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
      languageVersion.set(KotlinVersion.KOTLIN_1_9)
      jvmTarget.set(JvmTarget.JVM_17)
      freeCompilerArgs.add("-Xjvm-default=all")
      optIn.addAll(
        listOf(
          "kotlin.RequiresOptIn",
          "kotlin.ExperimentalStdlibApi",
          "kotlin.contracts.ExperimentalContracts",
          "kotlin.time.ExperimentalTime",
          "kotlinx.coroutines.ExperimentalCoroutinesApi",
          "kotlinx.coroutines.FlowPreview",
        ),
      )
      allWarningsAsErrors.set(false)
    }
  }
  extensions.configure<JavaPluginExtension> {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  extensions.findByType(CommonExtension::class.java)?.apply {
    namespace = "voice." + path.removePrefix(":").replace(':', '.')
    compileSdk = libs.findVersion("sdk-compile").get().requiredVersion.toInt()

    compileOptions.apply {
      isCoreLibraryDesugaringEnabled = true
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig.apply {
      minSdk = libs.findVersion("sdk-min").get().requiredVersion.toInt()
    }

    testOptions.apply {
      unitTests.apply {
        isReturnDefaultValues = true
        isIncludeAndroidResources = true
      }
      animationsDisabled = true
    }

    packaging.apply {
      jniLibs.apply {
        useLegacyPackaging = false
      }
    }
  }

  extensions.findByType(ApplicationExtension::class.java)?.apply {
    defaultConfig.apply {
      targetSdk = libs.findVersion("sdk-target").get().requiredVersion.toInt()
    }
  }

  dependencies.run {
    add("coreLibraryDesugaring", libs.findLibrary("desugar").get())
    if (project.path != ":logging:core") {
      add("implementation", project(":logging:core"))
    }
    add("implementation", platform(libs.findLibrary("compose-bom").get()))
    add("androidTestImplementation", platform(libs.findLibrary("compose-bom").get()))

    listOf(
      "coroutines.core",
      "coroutines.android",
    ).forEach {
      add("implementation", libs.findLibrary(it).get())
    }

    add("testImplementation", libs.findBundle("testing-jvm").get())
  }
}
