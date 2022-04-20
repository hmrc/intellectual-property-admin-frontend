/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import play.api.libs.json.JsPath

case object RepresentativeContactInternationalAddressPage extends QuestionPage[InternationalAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "representativeContactInternationalAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers
      .get(IsRepresentativeContactUkBasedPage)
      .map(ukBased => !ukBased)
}
