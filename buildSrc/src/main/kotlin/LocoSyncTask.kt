import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI
import java.util.zip.ZipFile

abstract class LocoSyncTask : DefaultTask() {
    @get:Input var apiKey: String = ""
    @get:Input var urlFormat: String = "https://localise.biz/api/export/archive/xml.zip?key=%s&format=android"
    @get:Internal var targetResDir: File = project.file("src/commonMain/composeResources")
    @get:Internal var buildDir: File = project.layout.buildDirectory.get().asFile

    init {
        group = "localization"
        description = "Sincroniza traducciones desde Loco"
    }

    @TaskAction
    fun run() {
        val url = String.format(urlFormat, apiKey)
        val tempZip = File(buildDir, "loco_translations.zip")
        val extractDir = File(buildDir, "loco_extracted")

        // 1. Descargar
        tempZip.parentFile.mkdirs()
        URI(url).toURL().openStream().use { input -> tempZip.outputStream().use { output -> input.copyTo(output) } }

        // 2. Extraer
        if (extractDir.exists()) extractDir.deleteRecursively()
        extractDir.mkdirs()
        ZipFile(tempZip).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val outFile = File(extractDir, entry.name)
                if (entry.isDirectory) outFile.mkdirs() else {
                    outFile.parentFile.mkdirs()
                    zip.getInputStream(entry).use { input -> outFile.outputStream().use { output -> input.copyTo(output) } }
                }
            }
        }

        // 3. Distribuir a composeResources
        extractDir.walkTopDown().filter { it.isFile && it.extension == "xml" }.forEach { sourceFile ->
            val langCode = sourceFile.parentFile.name
            if (langCode != extractDir.name && langCode != "mvi-xml-archive") {
                val androidFolder = when {
                    langCode == "values" || langCode.startsWith("values-") -> langCode
                    langCode.startsWith("en") -> "values"
                    else -> "values-$langCode"
                }
                val targetFile = File(targetResDir, "$androidFolder/strings.xml")
                targetFile.parentFile.mkdirs()
                sourceFile.copyTo(targetFile, overwrite = true)
                println("Actualizado: $langCode -> $androidFolder/strings.xml")
            }
        }
        tempZip.delete()
    }
}
