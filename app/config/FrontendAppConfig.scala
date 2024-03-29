/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
  val analyticsHost: String  = configuration.get[String](s"google-analytics.host")

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  lazy val isPlannedShutter: Boolean =
    configuration.get[Boolean]("isPlannedShutter")

  lazy val plannedShutterFromMessage: String = configuration.get[String]("plannedShutterAvailabilityMessage")

  lazy val timeoutPeriodSeconds: Int    = configuration.get[Int]("timeout.periodSeconds")
  lazy val timeoutCountdownSeconds: Int = configuration.get[Int]("timeout.countdownSeconds")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  val baseManageIprFrontendUrl: String =
    configuration.get[String]("intellectual-property-manage-apps-frontend.host")

  def manageIprHomeUrl          = s"$baseManageIprFrontendUrl/protect-intellectual-property-applications/manage-applications"
  def manageIprAccessibilityUrl =
    s"$baseManageIprFrontendUrl/protect-intellectual-property-applications/accessibility-statement"
}
