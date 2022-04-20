/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object AdditionalInfoProvidedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "additionalInfoProvided"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) =>
        for {
          first  <- userAnswers.remove(RestrictedHandlingPage)
          second <- first.remove(ShareWithEuropeanCommissionPage)
        } yield second

      case _ =>
        super.cleanup(value, userAnswers)
    }
}
