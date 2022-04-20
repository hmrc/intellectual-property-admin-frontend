/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import play.api.libs.json.JsPath

case object SecondaryTechnicalContactInternationalAddressPage extends QuestionPage[InternationalAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "secondaryTechnicalContactInternationalAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers
      .get(IsSecondaryTechnicalContactUkBasedPage)
      .map(ukBased => !ukBased)
  }

}

