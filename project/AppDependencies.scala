import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.11.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"                 %% "play-conditional-form-mapping-play-30" % "3.3.0",
    "uk.gov.hmrc"                 %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"                 %% "secure"                                % "8.2.0" exclude("org.bouncycastle", "bcprov-jdk15on"),
    "org.typelevel"               %% "cats-core"                             % "2.10.0",
    "uk.gov.hmrc"                 %% "play-frontend-hmrc-play-30"            % "11.13.0",
    "org.bouncycastle"             % "bcprov-jdk18on"                        % "1.80",
    "org.jsoup"                    % "jsoup"                                 % "1.15.3"       % Test,
    "org.scalatest"               %% "scalatest"                             % "3.2.16"       % Test,
    "org.scalatestplus.play"      %% "scalatestplus-play"                    % "5.1.0"        % Test
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatestplus" %% "scalacheck-1-17"        % "3.2.17.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
