lazy val root = (project in file(".")).enablePlugins(SbtWeb)

name := "test-project"

testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

testJs <<= testJs dependsOn (CoffeeScriptKeys.coffeescript in TestAssets)

testJsTestFiles := ((WebKeys.webTarget in TestAssets).value / "coffeescript" ** "*.spec.js").get ++
  ((baseDirectory in Test).value / "test" / "assets" / "javascripts" ** "*.spec.js").get

(sourceDirectory in TestAssets) := (baseDirectory in Test).value / "test" / "assets"