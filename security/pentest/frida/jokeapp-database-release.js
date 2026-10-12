const Types = {
  Joke: "e5.a",
  Database: "x4.a",
  Continuation: "k7.d"
}

Java.perform(function() {
  var Database = Java.use(Types.Database)
  var Joke = Java.use(Types.Joke)

  // Overload a private kotlin suspend function to return a hacked network response
  Database.d.overload(Types.Joke).implementation = function(joke) {
    // log the actual data
    console.log(joke)
    var spoofed = Joke.$new("jokeID", "This app is OWNED", null, true)
    this.d(spoofed)
    return null
  }
})