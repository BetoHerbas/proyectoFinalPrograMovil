import java.io.File
import java.util.Properties

object EnvLoader {
    private val envVars: Properties by lazy {
        val props = Properties()
        val envFile = File(".env")
        if (envFile.exists()) {
            envFile.bufferedReader().use { reader ->
                reader.lines().forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                        val firstEq = trimmed.indexOf("=")
                        val key = trimmed.substring(0, firstEq).trim()
                        val value = trimmed.substring(firstEq + 1).trim().removeSurrounding("\"").removeSurrounding("'")
                        props.setProperty(key, value)
                    }
                }
            }
        }
        props
    }

    fun get(key: String): String? {
        return envVars.getProperty(key) ?: System.getenv(key)
    }
}
