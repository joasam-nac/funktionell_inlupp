package org.example

import Movie
import com.mongodb.MongoException
import com.mongodb.kotlin.client.MongoClient
import kotlin.collections.orEmpty

// mongodb+srv://dev:<db_password>@cluster0.xudciah.mongodb.net/?appName=Cluster0
fun main() {
    val uri = System.getenv("MONGODB_URI") ?: run {
        println("kom ihåg att lägga in env MONGODB_URI")
        return
    }
    val chosenYear = 1975

    val movies: List<Movie> =
        MongoClient
            .create(uri)
            .use { client ->
                val rep = MovieRepository(client)
                    try {
                        rep.getMovies(chosenYear)
                    } catch (e: MongoException) {
                        println("Database error: ${e.message}")
                        e.printStackTrace()
                        emptyList()
                    }
    }
    println("================================inlämningsuppgift")

    println("Antal filmer från $chosenYear: ${movies.size}")

    val longestMovie = movies.maxByOrNull { it.runtime}
    println("Längsta film ${longestMovie?.title} med tid: ${longestMovie?.runtime}")

    println("Unika genrer från filmer $chosenYear: ${countUnqueGenres(movies)}")

    val topActors = getActorsFromTopRatedMovie(movies)
    println("Skådespelare i bästa filmen: $topActors")

    val leastActorsMovie = getMovieWithFewestActors(movies)
    println("Minst antal skådisar: ${leastActorsMovie?.title}")

    val actorInMostMovies = getActorInMostMovies(movies)
    println("Skådespelare i flest filmer: $actorInMostMovies")

    val uniqeLanguages = getUniqueLanguages(movies)
    println("Antal unika språk: ${uniqeLanguages.size}, ${uniqeLanguages.sorted()}")

    val anyUniqueTitles = hasSameNamedTitles(movies)
    println("Finns filmer med samma titlar: $anyUniqueTitles")
}

fun List<Movie>.flatMapFilterEmpty(movie: (Movie) -> List<String>?): Sequence<String> =
    asSequence()
    .flatMap { movie(it).orEmpty() }
    .map { it.trim()}
    .filter{it.isNotEmpty()}

fun getMovieWithFewestActors(movies: List<Movie>): Movie? {
    return movies
        .minByOrNull { it.cast.size }
                    //{ it.cast?.size ?: 0 }
}

fun countUnqueGenres(movies: List<Movie>): Int =
    movies.flatMapFilterEmpty{it.genres}
            .toSet()
            .size

fun getActorsFromTopRatedMovie(movies: List<Movie>): List<String> =
    movies
        .maxByOrNull { it.imdbRating } ?.cast.orEmpty()

fun getActorInMostMovies(movies: List<Movie>): String? =
    movies
        .flatMapFilterEmpty{it.cast}
            .groupingBy { it } //~~hashmap
            .eachCount()
            .maxByOrNull { it.value }?.key

fun getUniqueLanguages(movies: List<Movie>): Set<String> =
    movies
        .flatMapFilterEmpty { it.languages }
            .toSet()

fun hasSameNamedTitles(movies: List<Movie>): Boolean =
    movies
        .map { it.title.trim().lowercase() } //List<String>
        .filter { it.isNotEmpty() }
        .let { it.size != it.distinct().size }