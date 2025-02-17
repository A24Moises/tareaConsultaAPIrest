import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URL

const val API_KEY = "T3AiNIX3Av6CJL09ONVs8cqwRlfHxzQVFdpoHK1D"
const val BASE_URL = "https://api.nasa.gov/planetary/apod"

@Serializable
data class NasaResponse(
    val date: String,
    val explanation: String,
    val title: String,
    val url: String? = null, // Puede ser nulo si el JSON no lo tiene
    val hdurl: String? = null // Nueva propiedad opcional para imágenes en alta definición
)

fun getNasaData(date: String): NasaResponse? {
    val urlString = "$BASE_URL?api_key=$API_KEY&date=$date"
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    return try {
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = Json { ignoreUnknownKeys = true } // Ignorar claves desconocidas
        json.decodeFromString<NasaResponse>(response)
    } catch (e: Exception) {
        println("Error: ${e.message}")
        null
    } finally {
        connection.disconnect()
    }
}

fun main() {
    val dates = listOf("2024-02-12", "2024-02-11", "2024-02-10", "2024-02-09", "2024-02-08")

    dates.forEach { date ->
        val result = getNasaData(date)
        result?.let {
            println("📅 Fecha: ${it.date}")
            println("🌌 Título: ${it.title}")
            println("📝 Explicación: ${it.explanation.take(100)}...") // Solo los primeros 100 caracteres
            println("📷 Imagen: ${it.url ?: it.hdurl ?: "No disponible"}")
            println("--------------------------------------------------")
        }
    }
}
