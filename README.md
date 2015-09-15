sbt-web-test-js
==================

A sbt-web plugin that run tests within [Jasmine](https://github.com/jasmine/jasmine). Tests can be written in both Javascripts and Coffeescripts.

Important notes:

1. [playframework](https://github.com/playframework/playframework) uses sbt-web. Therefore, sbt-web-test-js can be used with playframework as well.
2. The plugin doesn't depend on sbt-web directly. However, it depends on sbt-web for compiling Coffeescripts and move all Javascripts into appropriate folders.
3. sbt-web-test-js assigns one test suite to one HTML file in order to enable encapsulation. Therefore, you can divide the test files in any way you want. For example, you might want all angular-based files in one suite and all jquery-based files in another suite.

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
  addSbtPlugin("com.fongmun" %% "sbt-web-test-js" % "1.2.1")
  ```

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
  [info] Loading project definition from /Users/tanin/Project/sbt-web-test-js/project
  [info] Loading project definition from /Users/tanin/Project/sbt-web-test-js/test-project/project
  [info] Updating {file:/Users/tanin/Project/sbt-web-test-js/}sbt-web-test-js...
  [info] Resolving org.fusesource.jansi#jansi;1.4 ...
  [info] Done updating.
  [info] Updating {file:/Users/tanin/Project/sbt-web-test-js/test-project/project/}plugins...
  [info] Resolving org.fusesource.jansi#jansi;1.4 ...
  [info] Done updating.
  [info] Compiling 1 Scala source to /Users/tanin/Project/sbt-web-test-js/target/scala-2.10/sbt-0.13/classes...
  [info] Set current project to test-project (in build file:/Users/tanin/Project/sbt-web-test-js/test-project/)
  [info] Updating {file:/Users/tanin/Project/sbt-web-test-js/test-project/}root...
  [info] Resolving org.fusesource.jansi#jansi;1.4 ...
  [info] Done updating.
  [info] CoffeeScript test compiling on 2 source(s)
  [info] Build /Users/tanin/Project/sbt-web-test-js/test-project/target/testjs/testjs_0.html for [test.spec.js]
  [info] Build /Users/tanin/Project/sbt-web-test-js/test-project/target/testjs/testjs_1.html for [test2.spec.js]
  ... PhantomJS logs can be ignored ...
  [info] Execute /Users/tanin/Project/sbt-web-test-js/test-project/target/testjs/testjs_0.html
  [info] Test from Coffeescript
  [info]  - Test from Coffeescript check the value from lib.coffee [passed]
  [info] Execute /Users/tanin/Project/sbt-web-test-js/test-project/target/testjs/testjs_1.html
  [info] Test from Javascripts
  [info]  - Test from Javascripts contains spec with an expectation [passed]
  [info] Total: 2, Success: 2, Failure: 0
  [success] Total time: 6 s, completed Sep 15, 2015 7:51:00 PM
  ```

  Please note that you can see the HTML files generated for the Jasmine tests.
  If you'd like to debug, you can directly open those files in a browser.


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
which is defaulted to `(target in test).value / "testjs"`; The directory often is `target/testjs`.


Contributors
---------------

* Tanin Na Nakorn [@tanin](http://twitter.com/tanin)


License
----------

This library is released under the Apache Software License, version 2, which should be included with the source in a file named LICENSE.

