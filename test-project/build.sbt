lazy val root = (project in file(".")).enablePlugins(SbtWeb)

name := "test-project"

testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

testJs <<= testJs dependsOn (WebKeys.assets in TestAssets)

testJsSuites := Seq(
  TestJsSuite(
    libs = (WebKeys.public in TestAssets).value / "lib" / "lib.js",
    tests = (WebKeys.public in TestAssets).value / "javascripts" / "test.spec.js"
  ),
  TestJsSuite(
    libs = (WebKeys.public in TestAssets).value / "lib" / "lib2.js",
    tests = (WebKeys.public in TestAssets).value / "javascripts" / "test2.spec.js"
  )
)

(sourceDirectory in TestAssets) := (baseDirectory in Test).value / "test" / "assets"
