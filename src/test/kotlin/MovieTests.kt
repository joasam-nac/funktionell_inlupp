import org.example.countUniqueGenres
import org.example.getActorInMostMovies
import org.example.getActorsFromTopRatedMovie
import org.example.getMovieWithFewestActors
import org.example.getUniqueLanguages
import org.example.hasSameNamedTitles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MovieTests {

    lateinit var movies: List<Movie>


    @BeforeEach
    fun setup() {
        movies = listOf(
            Movie("1", "The Godfather", 1972, listOf("Drama", "Crime"), "Francis Ford Coppola",
                listOf("Marlon Brando", "Al Pacino", "James Caan"), 9.2, listOf("English", "Italian"), 175),

            Movie("2", "Jaws", 1975, listOf("Thriller", "Drama"), "Steven Spielberg",
                listOf("Roy Scheider", "Robert Shaw", "Richard Dreyfuss"), 8.0, listOf("English"), 124),

            Movie("3", "One Flew Over the Cuckoo's Nest", 1975, listOf("Drama"), "Milos Forman",
                listOf("Jack Nicholson", "Roy Scheider"), 8.7, listOf("English", "Spanish"), 133),

            Movie("4", "Barry Lyndon", 1975, listOf("Drama", "Adventure"), "Stanley Kubrick",
                listOf("Ryan O'Neal", "Marisa Berenson"), 8.1, listOf("English", "French", "German"), 185),

            Movie("5", "The Godfather", 1972, listOf("Drama", "Crime"), "Francis Ford Coppola",
                listOf("Al Pacino"), 8.9, listOf("English"), 202),

            Movie("6", "Silent Film", 1975, listOf("  ", ""), "Unknown",
                listOf(), 5.0, listOf(), 90),

            Movie("7", "Jaws 2", 1978, listOf("Thriller", "Drama"), "Jeannot Szwarc",
                listOf("Roy Scheider", "Lorraine Gary", "Murray Hamilton"), 5.8, listOf("English"), 116),
        )
    }

    @Test
    @DisplayName("Should return movie with fewest actors")
    fun shouldGetFewestActors() {
        val movie = getMovieWithFewestActors(movies)
        Assertions.assertEquals("Silent Film", movie!!.title)
        Assertions.assertEquals(0, movie.cast.size)
    }

    @Test
    @DisplayName("Should return amount of unique genres")
    fun shouldGetAmountOfUniqueGenres() {
        val uniqueGenres = countUniqueGenres(movies)
        Assertions.assertEquals(4, uniqueGenres)
    }

    @Test
    @DisplayName("Should return list of actors from top rated movie")
    fun shouldGetActorsFromTopRatedMovie() {
        val actors = getActorsFromTopRatedMovie(movies)
        Assertions.assertTrue(actors.isNotEmpty())
        Assertions.assertEquals(3, actors.size)
    }

    @Test
    @DisplayName("Should return actor who plays in most movies")
    fun shouldGetActorFromMostMovies() {
        val actor = getActorInMostMovies(movies)
        Assertions.assertNotNull(actor)
        Assertions.assertEquals("Roy Scheider", actor)
    }

    @Test
    @DisplayName("Should return unique languages from the movie list")
    fun shouldGetUniqueLanguagesFromTheMovieList() {
        val languages = getUniqueLanguages(movies)
        Assertions.assertTrue(languages.isNotEmpty())
        Assertions.assertEquals(5, languages.size)
    }

    @Test
    @DisplayName("Should return true if there are movies with same title")
    fun shouldReturnTrueIfTheMovieWithSameTitle() {
        val moviesWithSameTitle = hasSameNamedTitles(movies)
        Assertions.assertTrue(moviesWithSameTitle)
    }





}