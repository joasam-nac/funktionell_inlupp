package org.example

//import Movie
import com.mongodb.kotlin.client.MongoClient
import org.bson.Document

// mongodb+srv://dev:<db_password>@cluster0.xudciah.mongodb.net/?appName=Cluster0
fun main() {
    val uri = System.getenv("MONGODB_URI")
    /*val movies: List<Movie> =
        try {
            getMovies(uri)
        } catch (e: Exception) {
            println("MongoDB error: ${e.message}")
            e.printStackTrace()
            return
        }

    val countMovies = movies.count() { it.year == 1975}*/
    val countMovies = moviesFromYear(uri, 1975)
    println(countMovies)

}

/*
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
    }*/

fun moviesFromYear(uri: String, year: Int): Int =
    MongoClient.create(uri).use {
        client -> client
            .getDatabase("sample_mflix")
            .getCollection<Document>("movies")
            .countDocuments(Document("year", year)).toInt()
    }

