package org.example

import Movie
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.MongoClient
import com.mongodb.kotlin.client.MongoCollection
import org.bson.Document

class MovieRepository(
    client: MongoClient,
    db: String = "sample_mflix",
    name: String = "movies"
) {
    private val collection: MongoCollection<Document> =
        client.getDatabase(db).getCollection(name)

    fun getMovies(year: Int): List<Movie> =
        collection
            .find(eq("year", year))
            .toList()
            .mapNotNull { doc ->
                runCatching { Movie.fromDocument(doc) }
                    .onFailure {
                        println("Parse error: ${it.message}")
                    }
                    .getOrNull()
            }
}