/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object AddAnotherLegalContactPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "addAnotherLegalContact"

    override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
      value match {
        case Some(false) =>
          for {
            first  <- userAnswers.remove(WhoIsSecondaryLegalContactPage)
            second  <- first.remove(IsApplicantSecondaryLegalContactUkBasedPage)
            third <- second.remove(ApplicantSecondaryLegalContactUkAddressPage)
            fourth  <- third.remove(ApplicantSecondaryLegalContactInternationalAddressPage)
          } yield fourth

        case _ =>
          super.cleanup(value, userAnswers)
      }

}
