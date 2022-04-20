/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UserAnswers, WhoIsSecondaryLegalContact}
import play.api.libs.json.JsPath

case object WhoIsSecondaryLegalContactPage extends QuestionPage[WhoIsSecondaryLegalContact] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whoIsSecondaryLegalContact"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers
      .get(AddAnotherLegalContactPage)
      .map(answer => answer)

}
