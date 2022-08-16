import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "logback-json-logger"            % "5.2.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "6.4.0",
    "uk.gov.hmrc"       %% "crypto"                         % "6.1.0",
    "uk.gov.hmrc"       %% "secure"                         % "8.1.0",
    "org.typelevel"     %% "cats-core"                      % "2.8.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "3.22.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatestplus.play"      %%  "scalatestplus-play"   % "5.1.0",
    "org.pegdown"                 %   "pegdown"              % "1.6.0",
    "org.jsoup"                   %   "jsoup"                % "1.15.2",
    "com.typesafe.play"           %%  "play-test"            % PlayVersion.current,
    "org.mockito"                 %   "mockito-all"          % "2.0.2-beta",
    "org.scalatestplus"           %%  "mockito-3-4"          % "3.2.10.0",
    "org.scalatestplus"           %%  "scalacheck-1-14"      % "3.2.2.0",
    "com.vladsch.flexmark"        %   "flexmark-all"         % "0.36.8",
    "org.scalacheck"              %%  "scalacheck"           % "1.16.0",
    "com.github.tomakehurst"      %   "wiremock-standalone"  % "2.27.2"
  ).map(_ % "test,it")

  val overrides = Seq(
    "commons-codec" % "commons-codec" % "1.15"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
