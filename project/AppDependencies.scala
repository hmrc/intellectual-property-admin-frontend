import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "play-conditional-form-mapping-play-30" % "2.0.0",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "uk.gov.hmrc"   %% "secure"                                % "8.2.0",
    "org.typelevel" %% "cats-core"                             % "2.10.0",
    "uk.gov.hmrc"   %% "play-frontend-hmrc-play-30"            % "8.5.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatestplus" %% "scalacheck-1-17"        % "3.2.17.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
