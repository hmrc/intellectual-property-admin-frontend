import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "logback-json-logger"            % "5.1.0",
    "uk.gov.hmrc"       %% "govuk-template"                 % "5.75.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.20.0",
    "uk.gov.hmrc"       %% "crypto"                         % "6.1.0",
    "uk.gov.hmrc"       %% "secure"                         % "8.1.0",
    "org.typelevel"     %% "cats-core"                      % "2.7.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "3.4.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"      %%  "scalatestplus-play"   % "5.1.0",
    "org.pegdown"                 %   "pegdown"              % "1.6.0",
    "org.jsoup"                   %   "jsoup"                % "1.13.1",
    "com.typesafe.play"           %%  "play-test"            % PlayVersion.current,
    "org.mockito"                 %   "mockito-all"          % "2.0.2-beta",
    "org.scalatestplus"           %%  "mockito-3-4"          % "3.2.9.0",
    "org.scalatestplus"           %%  "scalacheck-1-14"      % "3.2.0.0",
    "com.vladsch.flexmark"        %   "flexmark-all"         % "0.36.8",
    "org.scalacheck"              %%  "scalacheck"           % "1.14.3",
    "com.github.tomakehurst"      %   "wiremock-standalone"  % "2.27.1"
  ).map(_ % "test,it")

  val overrides = Seq(
    "commons-codec" % "commons-codec" % "1.12"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
