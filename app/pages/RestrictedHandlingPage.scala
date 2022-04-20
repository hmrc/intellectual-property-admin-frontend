/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

case object RestrictedHandlingPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "isRestrictedHandling"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {

    answers
      .get(AdditionalInfoProvidedPage)
      .map(answer => answer)

  }
}
