import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.HttpURLConnection
import java.net.URI

abstract class LocoUploadTask : DefaultTask() {
    @get:Input var apiKey: String = ""
    @get:Input var locale: String = "en"
    @get:Internal var sourceFile: File = project.file("src/commonMain/composeResources/values/strings.xml")

    init {
        group = "localization"
        description = "Sube el archivo base strings.xml a Loco"
    }

    @TaskAction
    fun run() {
        if (apiKey.isBlank()) {
            throw IllegalArgumentException("LOCO_API_KEY no configurado o vacío")
        }
        if (!sourceFile.exists()) {
            throw IllegalArgumentException("Archivo base no encontrado en la ruta: ${sourceFile.absolutePath}")
        }

        println("Subiendo archivo: ${sourceFile.absolutePath} (idioma: $locale) a Loco...")

        // La API de Loco espera un POST a /api/import/xml con el contenido del archivo en el cuerpo.
        // index=id es fundamental para usar los nombres de los recursos Android (el atributo 'name') como el ID del asset.
        // locale especifica el idioma del archivo que estamos importando.
        val urlString = "https://localise.biz/api/import/xml?key=$apiKey&index=id&locale=$locale"
        val connection = URI(urlString).toURL().openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/xml")

            // Escribir el archivo en el cuerpo del request
            connection.outputStream.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage

            if (responseCode in 200..299) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                println("¡Subida exitosa! Código de respuesta: $responseCode - $responseMessage")
                println("Resultado de la importación: $responseText")
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                throw RuntimeException("Error al subir a Loco. Código: $responseCode - $responseMessage. Detalles: $errorText")
            }
        } finally {
            connection.disconnect()
        }
    }
}
