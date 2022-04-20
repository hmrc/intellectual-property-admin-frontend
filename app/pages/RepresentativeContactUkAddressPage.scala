/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import play.api.libs.json.JsPath

case object RepresentativeContactUkAddressPage extends QuestionPage[UkAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "representativeContactUkAddress"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers.get(IsRepresentativeContactUkBasedPage)
}
