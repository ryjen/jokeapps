const Types = {
  Service: "t4.f",
  Response: "w4.a",
  Continuation: "k7.d"
}

Java.perform(function() {
  var Service = Java.use(Types.Service)
  var Response = Java.use(Types.Response)

  // Overload a private kotlin suspend function to return a hacked network response
  Service.a.overload(Types.Continuation).implementation = function(continuation) {
    var actual = this.a(continuation)
    // suspend functions pass a flag to indicate will not return immediately
    if (actual.toString() == "COROUTINE_SUSPENDED") {
      return actual
    }
    // log the actual data
    console.log(actual)
    // return the spoofed data
    return Response.$new(7, "hackID", "This app is HACKED", 200)
  }
})