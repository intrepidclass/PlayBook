import java.net.URI
import java.io.FileOutputStream

plugins {
  id("voice.library")
}

val ffmpegFileName = "ffmpeg-kit-full-gpl-6.0-2.LTS.aar"
// Use build directory instead of source libs directory
val ffmpegAarFile = layout.buildDirectory.file("downloaded-libs/$ffmpegFileName")
val ffmpegDownloadUrl = "https://artifactory.appodeal.com/appodeal-public/com/arthenica/ffmpeg-kit-full-gpl/6.0-2.LTS/ffmpeg-kit-full-gpl-6.0-2.LTS.aar"

abstract class DownloadFfmpeg : DefaultTask() {
    @get:Input
    abstract val downloadUrl: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun download() {
        val url = downloadUrl.get()
        val file = outputFile.get().asFile

        println("Downloading FFmpeg to: ${file.absolutePath}")

        file.parentFile.mkdirs()
        if (file.exists()) file.delete()

        try {
            URI(url).toURL().openStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            println("Download complete. Size: ${file.length()}")
        } catch (e: Exception) {
            throw GradleException("Download failed", e)
        }
    }
}

val downloadTask = tasks.register<DownloadFfmpeg>("downloadFfmpeg") {
    group = "download"
    downloadUrl.set(ffmpegDownloadUrl)
    outputFile.set(ffmpegAarFile)
    outputs.upToDateWhen { outputFile.get().asFile.exists() }
}

// Make sure preBuild waits for this
tasks.named("preBuild").configure {
  dependsOn(downloadTask)
}

dependencies {
  // Use the task provider to ensure dependency is lazy and triggers the task
  compileOnly(files(downloadTask))

  api("com.arthenica:smart-exception-java:0.2.1")
  implementation(libs.androidxCore)
}
