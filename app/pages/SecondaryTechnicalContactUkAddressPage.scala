/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import play.api.libs.json.JsPath

case object SecondaryTechnicalContactUkAddressPage extends QuestionPage[UkAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "secondaryTechnicalContactUkAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers.get(IsSecondaryTechnicalContactUkBasedPage)
  }

}
