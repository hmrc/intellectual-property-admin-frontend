/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import play.api.libs.json.JsPath

case object ApplicantLegalContactInternationalAddressPage extends QuestionPage[InternationalAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "applicantLegalContactInternationalAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers
      .get(IsApplicantLegalContactUkBasedPage)
      .map(ukBased => !ukBased)
}
