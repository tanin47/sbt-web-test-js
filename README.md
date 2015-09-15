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


Why we need it
----------------

Please see [our blog](http://tech.fongmun.com/post/129065417782/test-javascripts-and-coffeescripts-in)


How to use it
---------------

1. Add sbt-web-test-js into plugins.sbt:

  ```scala
  addSbtPlugin("com.fongmun" %% "sbt-web-test-js" % "1.0.0")
  ```

  * Please note that we are waiting for the approval of [OSSRH-17664](https://issues.sonatype.org/browse/OSSRH-17664).
  So, we can publish our plugin to Maven Central

2. Configure the sbt-web-test-js in build.sbt:

  ```scala
  testJsPhantomJsBinPath := "/usr/local/bin/phantomjs"

  // The Coffeescript files should be compiled before running tests,
  // and the compiled Javascripts and pure Javascripts files should be moved to (WebKeys.public in TestAssets)
  // (WebKeys.assets in TestAssets) does exactly that.
  testJs <<= testJs dependsOn (WebKeys.assets in TestAssets)

  // Each suite might have its own required library
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
  ```

  If every suite requires the same library, we can harness the power of Scala to shorten the code:

  ```
  testJsSuites := ((WebKeys.public in TestAssets).value / "javascripts" ** "*.spec.js").get.map { specFile =>
    TestJsSuite(
      libs = (WebKeys.public in TestAssets).value / "lib" / "lib2.js",
      tests = PathFinder(specFile)
    )
  }
  ```

3. Run `sbt testJs` and you'll see the result:

  ```bash
  [info] Running 2 suites
  [info] Test from Coffeescript
  [info]  - Test from Coffeescript contains spec with an expectation [passed]
  [info] Test from Javascripts
  [info]  - Test from Javascripts contains spec with an expectation [passed]
  [info] Total: 2, Success: 2, Failure: 0
  [success] Total time: 3 s, completed Sep 14, 2015 5:20:22 PM
  ```


Integrate with sbt test
------------------------------------

You can also hook it to your test command by adding the below line to build.sbt:

```scala
(test in Test) <<= (test in Test) dependsOn testJs
```

Now `sbt test` will also run `sbt testJs`


How it works internally
--------------------------

An HTML page is generated for each suite and is executed with PhantomJS.

If you'd like to debug each suite, please look at the HTML files in the directory of `testJsOutputDir`,
which is defaulted to `(target in test).value / "testjs"`; It often is `target/testjs`.


Contributors
---------------

* Tanin Na Nakorn [@tanin](http://twitter.com/tanin)


License
----------

This library is released under the Apache Software License, version 2, which should be included with the source in a file named LICENSE.

