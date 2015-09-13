sbt-web-test-js
==================

A sbt-web plugin that run tests within [Jasmine](https://github.com/jasmine/jasmine). Tests can be written in both Javascripts and Coffeescripts.

There are 2 things to note:

1. [playframework](https://github.com/playframework/playframework) uses sbt-web. Therefore, sbt-web-test-js can be used with playframework as well.
2. The plugin doesn't depend on sbt-web directly. However, it depends on sbt-web for compiling Coffeescripts and move all Javascripts into appropriate folders.

Requirement
--------------

- [PhantomJS](http://phantomjs.org/): because we run Jasmine tests using PhantomJS
- Scala 2.10.4+


How to use it
---------------

1. Add sbt-web-test-js into plugins.sbt:

  ```scala
  addSbtPlugin("com.fongmun" %% "sbt-web-test-js" % "1.0.0")
  ```

2. Configure the sbt-web-test-js in build.sbt:

  ```scala
  testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

  // The Coffeescript files should be compiled before running tests,
  // and the compiled Javascripts and pure Javascripts files should be moved to (WebKeys.public in TestAssets)
  // (WebKeys.assets in TestAssets) does exactly that.
  testJs <<= testJs dependsOn (WebKeys.assets in TestAssets)

  testJsTestFiles := ((WebKeys.public in TestAssets).value / "javascripts" ** "*.spec.js")
  ```

3. Run `sbt testJs`

### Run Javascripts tests with sbt test

You can also hook it to your test command by adding the below line to build.sbt:

```scala
(test in Test) <<= (test in Test) dependsOn testJs
```

Now `sbt test` will also run `sbt testJs`

Contributors
---------------

* Tanin Na Nakorn [@tanin](http://twitter.com/tanin)


License
----------

This library is released under the Apache Software License, version 2, which should be included with the source in a file named LICENSE.

