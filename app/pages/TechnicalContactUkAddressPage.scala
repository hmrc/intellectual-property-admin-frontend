/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import play.api.libs.json.JsPath

case object TechnicalContactUkAddressPage extends QuestionPage[UkAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "technicalContactUkAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers.get(IsTechnicalContactUkBasedPage)
}
