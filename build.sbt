import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "intellectual-property-admin-frontend"

ThisBuild / scalaVersion := "3.3.7"
ThisBuild / majorVersion := 0

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(inConfig(Test)(testSettings) *)
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._",
      "viewmodels._"
    ),
    PlayKeys.playDefaultPort := 9876,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController;.*ConsignmentConnector.*",
    coverageExcludedPackages := ".*testonly.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq(
      // Twirl-generated Scala (views)
      "-Wconf:msg=unused import&src=.*/target/scala-.*/.*views.*:s",
      "-Wconf:msg=unused import&src=.*/src_managed/.*:s",

      // Play routes-generated Scala
      "-Wconf:msg=unused import&src=.*/conf/.*:s",
      "-Wconf:msg=unused import&src=.*/routes/.*:s",

      // Silence unused pattern variables from Play routes
      "-Wconf:msg=unused pattern variable&src=.*/conf/.*:s",
      "-Wconf:msg=unused pattern variable&src=.*/routes/.*:s",

      "-feature"
    ),
    scalafmtOnCompile := true,
    libraryDependencies ++= AppDependencies(),
    excludeDependencies += ExclusionRule("org.lz4", "lz4-java"),
    retrieveManaged := true,
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat)
  )

lazy val testSettings: Seq[Def.Setting[?]] = Seq(
  fork := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf"
  ),
  unmanagedSourceDirectories ++= Seq(
    baseDirectory.value / "test-utils"
  )
)
