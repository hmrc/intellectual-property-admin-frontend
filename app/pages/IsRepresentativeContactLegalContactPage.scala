/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

import scala.util.Try

case object IsRepresentativeContactLegalContactPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isRepresentativeContactLegalContact"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(true) =>
        if (userAnswers.get(IsRepresentativeContactUkBasedPage).contains(true)) {
          for {
            first <- userAnswers.set(ApplicantLegalContactPage, userAnswers.get(RepresentativeDetailsPage).get.getAsLegal)
            second <- first.set(IsApplicantLegalContactUkBasedPage, userAnswers.get(IsRepresentativeContactUkBasedPage).get)
            third <- second.set(ApplicantLegalContactUkAddressPage, userAnswers.get(RepresentativeContactUkAddressPage).get)
            fourth <- third.remove(ApplicantLegalContactInternationalAddressPage)
          } yield fourth

        } else {
          for {
            first <- userAnswers.set(ApplicantLegalContactPage, userAnswers.get(RepresentativeDetailsPage).get.getAsLegal)
            second <- first.set(IsApplicantLegalContactUkBasedPage, userAnswers.get(IsRepresentativeContactUkBasedPage).get)
            third <- second.remove(ApplicantLegalContactUkAddressPage)
            fourth <- third.set(ApplicantLegalContactInternationalAddressPage, userAnswers.get(RepresentativeContactInternationalAddressPage).get)
          } yield fourth
        }


      case _ =>
        super.cleanup(value, userAnswers)
    }
}

