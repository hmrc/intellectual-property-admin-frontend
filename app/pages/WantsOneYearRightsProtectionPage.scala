/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

case object WantsOneYearRightsProtectionPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "wantsOneYearRightsProtection"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers.get(IsExOfficioPage)
}
