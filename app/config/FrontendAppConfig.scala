/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration) {

  val analyticsToken: String = configuration.get[String](s"google-analytics.token")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val isPlannedShutter: Boolean =
    configuration.get[Boolean]("isPlannedShutter")

  lazy val plannedShutterFromMessage: String = configuration.get[String]("plannedShutterAvailabilityMessage")

  lazy val timeoutPeriodSeconds: Int = configuration.get[Int]("timeout.periodSeconds")
  lazy val timeoutCountdownSeconds: Int = configuration.get[Int]("timeout.countdownSeconds")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  val baseManageIprFrontendUrl: String =
    configuration.get[String]("intellectual-property-manage-apps-frontend.host")

  def manageIprHomeUrl = s"$baseManageIprFrontendUrl/protect-intellectual-property-applications/manage-applications"
  def manageIprAccessibilityUrl = s"$baseManageIprFrontendUrl/protect-intellectual-property-applications/accessibility-statement"
}
