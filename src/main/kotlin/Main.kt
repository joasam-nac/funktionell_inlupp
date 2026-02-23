package org.example

import Movie
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.MongoClient
import org.bson.Document
import kotlin.collections.orEmpty

// mongodb+srv://dev:<db_password>@cluster0.xudciah.mongodb.net/?appName=Cluster0
fun main() {
    val uri = System.getenv("MONGODB_URI")
    val chosenYear = 1975
    val movies: List<Movie> =
        try {
            getMovies(uri, chosenYear)
        } catch (e: Exception) {
            println("MongoDB error: ${e.message}")
            e.printStackTrace()
            return
        }


    //movies = movies.filter{ it.year == chosenYear }

    println("Antal filmer från $chosenYear: ${movies.count()}")

    val longestMovie = movies.maxByOrNull { it.runtime}
    println("Längsta film ${longestMovie?.title} med tid: ${longestMovie?.runtime}")

    println("Unika genrer från filmer $chosenYear: ${countUnqueGenres(movies)}")

    val topActors = getActorsFromTopRatedMovie(movies)
    println(topActors)

    val leastActorsMovie = getMovieWithFewestActors(movies)
    println("Minst antal skådisar: ${leastActorsMovie?.title}")

    val actorInMostMovies = getActorInMostMovies(movies)
    println("Skådespelare i flest filmer: $actorInMostMovies")

    val uniqeLanguages = getUniqueLanguages(movies)
    println("Antal unika språk: ${uniqeLanguages.count()}, ${uniqeLanguages.sorted()}")

    val anyUniqueTitles = hasSameNamedTitles(movies)
    println(anyUniqueTitles)
}


fun getMovies(uri: String, year: Int): List<Movie> =
    MongoClient.create(uri).use { client ->
        client
            .getDatabase("sample_mflix")
            .getCollection<Document>("movies")
            .find(eq("year", year)) // gör hämtning mindre
            .toList()
            .asSequence()
            .mapNotNull { document -> runCatching { Movie.fromDocument(document) }.getOrNull() }
            .toList()
    }



fun getMovieWithFewestActors(movies: List<Movie>): Movie? {
    return movies
        .minByOrNull { it.cast.orEmpty().size }
                        //{ it.cast?.size ?: 0 }
}

fun countUnqueGenres(movies: List<Movie>): Int =
    movies
        .asSequence() //rek från ide
        .flatMap { it.genres.orEmpty() }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()
            .size

fun getActorsFromTopRatedMovie(movies: List<Movie>): List<String> =
    movies
        .maxByOrNull { it.imdbRating } ?.cast.orEmpty()

fun getActorInMostMovies(movies: List<Movie>): String? =
    movies
        .asSequence() // rek från ide
        .flatMap{it.cast.orEmpty() } //List<String>
            .map{it.trim()}
            .filter{it.isNotEmpty()}
            .groupingBy { it } //~~hashmap
            .eachCount()
            .maxByOrNull { it.value }?.key

fun getUniqueLanguages(movies: List<Movie>): Set<String> =
    movies
        .asSequence() //rek från ide
        .flatMap { it.languages.orEmpty() }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

fun hasSameNamedTitles(movies: List<Movie>): Boolean =
    movies
        .mapNotNull { it.title?.trim()?.lowercase() } //List<String>
        .filter { it.isNotEmpty() }
        .run { size != toSet().size } //set har endast unika