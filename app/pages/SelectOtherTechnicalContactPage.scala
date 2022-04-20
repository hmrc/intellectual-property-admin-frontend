/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{ContactOptions, UserAnswers}
import play.api.libs.json.JsPath

case object SelectOtherTechnicalContactPage extends QuestionPage[ContactOptions] {

  override def path: JsPath = JsPath \ toString

  override def isRequired(answers: UserAnswers): Option[Boolean] = {
    answers
      .get(AddAnotherTechnicalContactPage)
      .map(answer => answer)
  }

  override def toString: String = "selectOtherTechnicalContact"

}
