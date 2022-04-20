/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsApplicantSecondaryLegalContactUkBasedPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isApplicantSecondaryLegalContactUkBased"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers
      .get(AddAnotherLegalContactPage)
      .map(answer => answer)
  }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true)  =>
        userAnswers.remove(ApplicantSecondaryLegalContactInternationalAddressPage)
      case Some(false) =>
        userAnswers.remove(ApplicantSecondaryLegalContactUkAddressPage)
      case _ =>
        super.cleanup(value, userAnswers)
    }
}
