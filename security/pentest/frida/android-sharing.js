const Types = {
  Intent: "android.content.Intent",
  CharSequence: "java.lang.CharSequence",
  Context: "android.content.Context"
}

Java.perform(function() {
  var Intent = Java.use(Types.Intent)

  // The spoofed input
  var spoofed = "Please visit hacked link https://example.com"

  Intent.createChooser.overload(Types.Intent, Types.CharSequence).implementation = function(intent, title) {
    if (intent.getAction() != "android.intent.action.SEND") {
      return intent
    }
    console.log(intent.getStringExtra("android.intent.extra.TEXT"))
    intent.putExtra("android.intent.extra.TEXT", spoofed)
    return intent
  }
})