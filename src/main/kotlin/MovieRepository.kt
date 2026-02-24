package org.example

import Movie
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoCollection
import org.bson.Document

class MovieRepository(private val client: MongoClient, private val db: String = "sample_mflix", private val name: String = "movies") {
    private val rep: MongoCollection<Document>
        get() = client
            .getDatabase(db)
            .getCollection(name)

    fun getMovies(year: Int): List<Movie> =
        rep
            .find(eq("year", year))
            .toList()
            .mapNotNull { doc ->
                runCatching { Movie.fromDocument(doc) }
                    .onFailure { println("Parse error: ${it.message}") }
                    .getOrNull()
            }
}