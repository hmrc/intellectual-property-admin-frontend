import sbt._

object AppDependencies {
  import play.core.PlayVersion

  private val bootstrapVersion = "7.23.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "play-conditional-form-mapping" % "1.13.0-play-28",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-28"    % bootstrapVersion,
    "uk.gov.hmrc"   %% "secure"                        % "8.1.0",
    "org.typelevel" %% "cats-core"                     % "2.10.0",
    "uk.gov.hmrc"   %% "play-frontend-hmrc"            % "7.29.0-play-28"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatestplus.play" %% "scalatestplus-play"       % "5.1.0",
    "org.pegdown"             % "pegdown"                  % "1.6.0",
    "org.jsoup"               % "jsoup"                    % "1.15.4",
    "com.typesafe.play"      %% "play-test"                % PlayVersion.current,
    "org.mockito"             % "mockito-all"              % "2.0.2-beta",
    "org.scalatestplus"      %% "mockito-3-4"              % "3.2.10.0",
    "org.scalatestplus"      %% "scalacheck-1-14"          % "3.2.2.0",
    "com.vladsch.flexmark"    % "flexmark-all"             % "0.36.8",
    "org.scalacheck"         %% "scalacheck"               % "1.17.0",
    "com.github.tomakehurst"  % "wiremock-standalone"      % "2.27.2"
  ).map(_ % "test,it")

  val overrides: Seq[ModuleID] = Seq(
    "commons-codec" % "commons-codec" % "1.16.0"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
