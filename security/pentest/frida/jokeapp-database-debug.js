const Types = {
  Joke: "com.github.ryjen.jokeapp.domain.model.Joke",
  Database: "com.github.ryjen.jokeapp.data.repository.joke.JokeRepository",
  Continuation: "kotlin.coroutines.Continuation",
}

Java.perform(function() {
  var Database = Java.use(Types.Database)
  var Joke = Java.use(Types.Joke)

  // Overload a private kotlin suspend function to return a hacked network response
  Database.addFavorite.overload(Types.Joke, Types.Continuation).implementation = function(joke, continuation) {
    // suspend functions pass a flag to indicate will not return immediately
    if (continuation.toString() == "COROUTINE_SUSPENDED") {
      this.addFavorite(joke, continuation)
      return null
    }
    // log the actual data
    console.log(joke)
    var spoofed = Joke.$new("jokeID", "This joke is OWNED", null, true)
    this.addFavorite(spoofed, continuation)
    return null // kotlin unit
  }
})