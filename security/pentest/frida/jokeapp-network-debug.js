const Types = {
  Service: "com.github.ryjen.jokeapp.data.api.JokeService",
  Response: "com.github.ryjen.jokeapp.data.model.JokeResponse",
  Continuation: "kotlin.coroutines.Continuation",
}
Java.perform(function() {
  var Service = Java.use(Types.Service)
  var Response = Java.use(Types.Response)

  // Overload a private kotlin suspend function to return a hacked network response
  Service.getRandomJoke.overload(Types.Continuation).implementation = function(continuation) {
    var actual = this.getRandomJoke(continuation)
    // suspend functions pass a flag to indicate will not return immediately
    if (actual.toString() == "COROUTINE_SUSPENDED") {
      return actual
    }
    // log the actual data
    console.log(actual)
    // return the spoofed data
    return Response.$new("hackID", "This app is HACKED", 200)
  }
})