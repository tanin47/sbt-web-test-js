package com.fongmun.testjs

import java.io.File
import java.util

import org.openqa.selenium.phantomjs.{PhantomJSDriver, PhantomJSDriverService}
import org.openqa.selenium.remote.DesiredCapabilities
import sbt.Keys._
import sbt._

import scala.collection.JavaConverters._

object TestJsPlugin extends AutoPlugin {
  object autoImport {
    val testJs = TaskKey[Unit]("testJs", "Run tests with Jasmine")

    val testJsTestFiles = SettingKey[PathFinder]("testJsTestFiles", "the files that contain tests")
    val testJsLibFiles = SettingKey[PathFinder]("testJsLibFiles", "the files that are necessary for tests (but not the tests themselves)")
    val testJsOutputDir = SettingKey[File]("testJsOutputDir", "directory to output files to")
    val testJsPhantomJsBinPath = SettingKey[String]("testJsPhantomJsBinPath", "The full path of PhantomJS executable")
    val testJsPhantomJsDriver = SettingKey[() => PhantomJSDriver]("testJsPhantomJsDriver")
  }
  import autoImport._
  override def requires = empty
  override def trigger = allRequirements
  override lazy val projectSettings = Seq(
    testJsOutputDir := (target in test).value / "testjs",
    testJsTestFiles := PathFinder.empty,
    testJsLibFiles := PathFinder.empty,
    testJsPhantomJsBinPath := "/usr/local/bin/phantomjs",
    testJsPhantomJsDriver := { () =>
      val capabilities = {
        val caps = new DesiredCapabilities()
        caps.setJavascriptEnabled(true)
        caps.setCapability("takesScreenshot", true)
        caps.setCapability(
          PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
          testJsPhantomJsBinPath.value
        )
        caps.setCapability(
          PhantomJSDriverService.PHANTOMJS_CLI_ARGS,
          Array[String](
            "--load-images=false",
            "--ignore-ssl-errors=yes",
            "--proxy-type=none",
            "--local-to-remote-url-access=true",
            "--ssl-protocol=TLSv1",
            "--disk-cache=true",
            "--max-disk-cache-size=200000",
            "--web-security=false"
          )
        )
        caps
      }
      new PhantomJSDriver(
      {
        new PhantomJSDriverService.Builder()
          .usingPhantomJSExecutable(new File(testJsPhantomJsBinPath.value))
          .usingGhostDriver(null)
          .usingAnyFreePort
          .withProxy(null)
          .withLogFile(null)
          .usingCommandLineArguments(
            capabilities.getCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS).asInstanceOf[Array[String]]
          )
          .usingGhostDriverCommandLineArguments(
            capabilities.getCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS).asInstanceOf[Array[String]]
          )
          .build()
      },
      capabilities
      )
    },
    testJs := {
      val logger = sLog.value
      val jasmineHtml = testJsOutputDir.value / "testjs.html"

      val allLibJsTags = generateTagsHtml(testJsLibFiles.value)
      val allTestJsTags = generateTagsHtml(testJsTestFiles.value)

      val jasmineDir = testJsOutputDir.value / "jasmine"
      val jasmineDirAbsolutePath = jasmineDir.getAbsolutePath
      extractJasmineFiles(jasmineDir)

      IO.write(
        jasmineHtml,
        s"""
          | <html>
          |   <head>
          |     <link rel="shortcut icon" type="image/png" href="file://$jasmineDirAbsolutePath/jasmine_favicon.png">
          |     <link rel="stylesheet" type="text/css" href="file://$jasmineDirAbsolutePath/jasmine.css">
          |
          |     <script type="text/javascript" src="file://$jasmineDirAbsolutePath/override.js"></script>
          |
          |     <script type="text/javascript" src="file://$jasmineDirAbsolutePath/jasmine.js"></script>
          |     <script type="text/javascript" src="file://$jasmineDirAbsolutePath/jasmine-html.js"></script>
          |     <script type="text/javascript" src="file://$jasmineDirAbsolutePath/boot.js"></script>
          |     <script type="text/javascript">
          |       jasmine.getEnv().addReporter(reporter);
          |     </script>
          |     $allLibJsTags
          |     $allTestJsTags
          |   </head>
          |   <body>
          |   </body>
          | </html>
        """.stripMargin
      )

      val browser = testJsPhantomJsDriver.value()
      browser.get(s"file://${jasmineHtml.getAbsolutePath}")
      browser.executeScript("return consoleOutputs;").asInstanceOf[util.ArrayList[String]].asScala.foreach { line =>
        logger.info(line)
      }

      val totalCount = browser.executeScript("return summary.total;").asInstanceOf[Long]
      val successCount = browser.executeScript("return summary.success;").asInstanceOf[Long]
      val failureCount = browser.executeScript("return summary.failure;").asInstanceOf[Long]
      val summaryText = s"Total: $totalCount, Success: $successCount, Failure: $failureCount"

      if (failureCount > 0) {
        sys.error(summaryText)
      } else {
        logger.info(summaryText)
      }
    }
  )

  def generateTagsHtml(path: PathFinder): String = {
    path.get
      .map(_.getAbsolutePath)
      .map { path =>
        s"""<script type="text/javascript" src="file://$path"></script>"""
      }
      .mkString("\n")
  }

  def extractJasmineFiles(parentDir: File): Unit = {
    List(
      "jasmine_favicon.png",
      "jasmine.css",
      "override.js",
      "jasmine.js",
      "jasmine-html.js",
      "boot.js"
    ).foreach { file =>
      transfer(s"jasmine/$file", parentDir / file)
    }
  }

  def transfer(resourcePath: String, dest: File): Unit = {
    val inputStream = this.getClass.getClassLoader.getResourceAsStream(resourcePath)

    IO.transfer(inputStream, dest)
    inputStream.close()
  }
}