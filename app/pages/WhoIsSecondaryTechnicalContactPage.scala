/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UserAnswers, TechnicalContact}
import play.api.libs.json.JsPath

case object WhoIsSecondaryTechnicalContactPage extends QuestionPage[TechnicalContact] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whoIsSecondaryTechnicalContact"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers
      .get(AddAnotherTechnicalContactPage)
      .map(answer => answer)
}
