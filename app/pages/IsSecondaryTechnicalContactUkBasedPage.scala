/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsSecondaryTechnicalContactUkBasedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers
      .get(AddAnotherTechnicalContactPage)
      .map(answer => answer)
  }

  override def toString: String = "isSecondaryTechnicalContactUkBased"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  =>
        userAnswers.remove(SecondaryTechnicalContactInternationalAddressPage)
      case Some(false) =>
        userAnswers.remove(SecondaryTechnicalContactUkAddressPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
