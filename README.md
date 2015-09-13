sbt-web-test-js
==================

sbt-web-test-js is a sbt-web plugin that run tests within [Jasmine](https://github.com/jasmine/jasmine). Tests can be written in both Javascripts and Coffeescripts.

There are 2 things to note:

1. [playframework](https://github.com/playframework/playframework) uses sbt-web. Therefore, sbt-web-test-js can be used with playframework as well.
2. This projects doesn't depend on sbt-web. However, it requires sbt-web in your project because of sbt-web's directory structure.

Requirement
--------------
- PhantomJS: because we run Jasmine tests using PhantomJS


How to use it
---------------

1. Add sbt-web-test-js into plugins.sbt:

```scala
addSbtPlugin("com.fongmun" % "sbt-web-test-js" % "1.0.0")
```

2. Configure the sbt-web-test-js in build.sbt:

```scala
testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

// The Coffeescript files should be compiled before running tests,
// and the compiled Javascripts and pure Javascripts files should be moved to (WebKeys.public in TestAssets)
testJs <<= testJs dependsOn (WebKeys.assets in TestAssets) 

testJsTestFiles := ((WebKeys.public in TestAssets).value / "javascripts" ** "*.spec.js")
```

3. Run `sbt testJs`

You can also hook it to your test command by adding the below line to build.sbt:

```scala
(test in Test) <<= (test in Test) dependsOn testJs
```

And you can run `sbt test`, and it will also run testJs.

Contributors:
---------------

* Tanin Na Nakorn [@tanin](http://twitter.com/tanin)


License
----------

This library is released under the Apache Software License, version 2, which should be included with the source in a file named LICENSE.

