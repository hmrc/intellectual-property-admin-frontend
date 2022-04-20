/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsTechnicalContactUkBasedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isTechnicalContactUkBased"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true) =>
        userAnswers.remove(TechnicalContactInternationalAddressPage)
      case Some(false) =>
        userAnswers.remove(TechnicalContactUkAddressPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }

}
