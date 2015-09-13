lazy val root = (project in file(".")).enablePlugins(SbtWeb)

name := "test-project"

testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

testJs <<= testJs dependsOn (WebKeys.assets in TestAssets)

testJsTestFiles := ((WebKeys.public in TestAssets).value / "javascripts" ** "*.spec.js")

(sourceDirectory in TestAssets) := (baseDirectory in Test).value / "test" / "assets"
