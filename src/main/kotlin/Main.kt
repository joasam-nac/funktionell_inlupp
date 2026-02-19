package org.example

import Movie
import com.mongodb.kotlin.client.MongoClient
import org.bson.Document
import kotlin.collections.orEmpty

// mongodb+srv://dev:<db_password>@cluster0.xudciah.mongodb.net/?appName=Cluster0
fun main() {
    val uri = System.getenv("MONGODB_URI")
    val movies: List<Movie> =
        try {
            getMovies(uri)
        } catch (e: Exception) {
            println("MongoDB error: ${e.message}")
            e.printStackTrace()
            return
        }


    //val countMovies = moviesFromYear(uri, 1975)

    val movies1975 = movies.filter{it.year == 1975}
    println("Antal filmer från 1975: ${movies1975.count()}")

    val longestMovie = movies.maxBy { it.runtime }
    println("Längsta film ${longestMovie.title} med tid: ${longestMovie.runtime}")

    val uniqueGenres1975 = countUniqueGenres(movies1975)
    println("Unika genrer från filmer 1975: $uniqueGenres1975")

    val topActors = getActorsFromTopRatedMovie(movies)
    println(topActors)

    val leastActorsMovie = movies.asSequence().minByOrNull { it.cast?.size ?: 0 }
    println("Minst antal skådisar: ${leastActorsMovie?.title}")

    val actorInMostMovies = getActorInMostMovies(movies)
    println("Skådespelare i flest filmer: $actorInMostMovies")

    val uniqeLanguages = getUniqueLanguages(movies)
    println("Antal unika språk: ${uniqeLanguages.count()}")

    val anyUniqueTitles = hasSameNamedTitles(movies)
    println(anyUniqueTitles)
}


fun getMovies(uri: String): List<Movie> =
    MongoClient.create(uri).use { client ->
        client
            .getDatabase("sample_mflix")
            .getCollection<Document>("movies")
            .find()
            .toList()
            .asSequence()
            .mapNotNull { document -> runCatching { Movie.fromDocument(document) }.getOrNull() }
            .toList()
    }

fun moviesFromYear(uri: String, year: Int): Int =
    MongoClient.create(uri).use {
        client -> client
            .getDatabase("sample_mflix")
            .getCollection<Document>("movies")
            .countDocuments(Document("year", year)).toInt()
    }

fun countUniqueGenres(movieCollection: List<Movie>): Int
    = movieCollection.flatMap{ it.genres.orEmpty()}
    .map{it.trim()}
    .filter{it.isNotEmpty()}.distinct().count()

fun getActorsFromTopRatedMovie(movies: List<Movie>): List<String> =
    movies.maxByOrNull { it.imdbRating } ?.cast.orEmpty()

fun getActorInMostMovies(movies: List<Movie>): String? =
    movies.flatMap{it.cast.orEmpty().asSequence() }
        .map{it.trim()}.filter{it.isNotEmpty()}.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key

fun getUniqueLanguages(movies: List<Movie>): List<String> =
    movies.flatMap{it.languages.orEmpty().asSequence()}.map{it.trim()}.filter{it.isNotEmpty()}.distinct().toList()

fun hasSameNamedTitles(movies: List<Movie>): Boolean =
    movies.map{ it.title?.trim()?.lowercase().orEmpty()}
        .filter{it.isNotEmpty()}
        .groupingBy{it}.eachCount().any{ (_, count) -> count >= 2}