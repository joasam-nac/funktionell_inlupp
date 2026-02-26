package org.example

import Movie
import com.mongodb.MongoException
import com.mongodb.kotlin.client.MongoClient
import kotlin.collections.orEmpty

// mongodb+srv://dev:<db_password>@cluster0.xudciah.mongodb.net/?appName=Cluster0

fun main() {
    val uri = System.getenv("MONGODB_URI")
        ?: return println("Kom ihåg att lägga in env MONGODB_URI")

    val chosenYear = 1975
    val movies = fetchMovies(uri, chosenYear)

    println("================================ inlämningsuppgift")
    println("Antal filmer från $chosenYear: ${movies.size}")

    movies.maxByOrNull { it.runtime }?.let {
        println("Längsta film ${it.title} med tid: ${it.runtime}")
    }

    println("Unika genrer: ${movies.uniqueGenresCount()}")

    println("Skådespelare i bästa filmen: ${movies.topRatedActors()}")

    println(
        "Minst antal skådisar: ${
            movies.movieWithFewestActors()?.title
        }"
    )

    println(
        "Skådespelare i flest filmer: ${
            movies.actorInMostMovies()
        }"
    )

    val languages = movies.uniqueLanguages()
    println("Antal unika språk: ${languages.size}, ${languages.sorted()}")

    println("Finns filmer med samma titlar: ${movies.hasDuplicateTitles()}")
}

fun fetchMovies(uri: String, year: Int): List<Movie> =
    MongoClient.create(uri).use { client ->
        runCatching {
            MovieRepository(client).getMovies(year)
        }.onFailure {
            if (it is MongoException) {
                println("Database error: ${it.message}")
            }
        }.getOrDefault(emptyList())
    }

private fun List<Movie>.flatMapNotBlank(
    selector: (Movie) -> List<String>?
): List<String> =
    flatMap { selector(it).orEmpty() }
        .map(String::trim)
        .filter(String::isNotBlank)

fun List<Movie>.movieWithFewestActors(): Movie? =
    minByOrNull { it.cast.size }

fun List<Movie>.uniqueGenresCount(): Int =
    flatMapNotBlank { it.genres }
        .toSet()
        .size

fun List<Movie>.topRatedActors(): List<String> =
    maxByOrNull { it.imdbRating }
        ?.cast
        .orEmpty()

fun List<Movie>.actorInMostMovies(): String? =
    flatMapNotBlank { it.cast }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key

fun List<Movie>.uniqueLanguages(): Set<String> =
    flatMapNotBlank { it.languages }
        .toSet()

fun List<Movie>.hasDuplicateTitles(): Boolean =
    map { it.title.trim().lowercase() }
        .filter(String::isNotBlank)
        .groupingBy { it }
        .eachCount()
        .any { it.value > 1 }