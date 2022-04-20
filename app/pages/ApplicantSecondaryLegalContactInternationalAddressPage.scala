/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import play.api.libs.json.JsPath

case object ApplicantSecondaryLegalContactInternationalAddressPage extends QuestionPage[InternationalAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "applicantSecondaryLegalContactInternationalAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers
      .get(IsApplicantSecondaryLegalContactUkBasedPage)
      .map(ukBased => !ukBased)
  }

}

