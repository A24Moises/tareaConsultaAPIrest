import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class Country(
    val name: Name,
    val population: Int,
    val continents: List<String>,
    val languages: Map<String, String>? = null
)

@Serializable
data class Name(
    val common: String,
    val official: String
)

fun main() {
    val client = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("https://restcountries.com/v3.1/all"))
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val jsonBody = response.body()

    if (response.statusCode() == 200) {
        val json = Json { ignoreUnknownKeys = true }  // 🔹 Ignorar campos desconocidos
        val countries = json.decodeFromString<List<Country>>(jsonBody)

        //Países ordenados por población de mayor a menor
        println("\n🌍 Top 10 países más poblados:\n")
        countries
            .sortedByDescending { it.population }
            .take(10)
            .forEach { country -> println("${country.name.common}: ${country.population} habitantes") }

        //Agrupar países por continente
        println("\n🌍 Países agrupados por continente:\n")
        val groupedByContinent = countries.groupBy { it.continents.firstOrNull() ?: "Desconocido" }
        groupedByContinent.forEach { (continent, countryList) ->
            println("🔹 $continent: ${countryList.size} países")
        }

        //Países con más de 50 millones de habitantes
        println("\n🏙️ Países con más de 50 millones de habitantes:\n")
        countries
            .filter { it.population > 50_000_000 }
            .forEach { country -> println("\uD83D\uDD39 ${country.name.common}: ${country.population} habitantes") }

        //Lista de idiomas únicos en el mundo
        println("\n🗣️ Lista de idiomas únicos hablados:\n")
        val uniqueLanguages = countries
            .flatMap { it.languages?.values ?: emptyList() }
            .distinct()
        println(uniqueLanguages.joinToString(", "))

        //Población total del planeta
        val totalPopulation = countries.sumOf { it.population.toLong() }
        println("\n🌏 Población total del mundo: $totalPopulation habitantes")

    } else {
        println("⚠️ Error al obtener los datos. Código: ${response.statusCode()}")
    }
}
