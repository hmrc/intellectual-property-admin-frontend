/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.UserAnswers
import play.api.libs.json.JsPath

case object ApplicationReceiptDatePage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "applicationReceiptDate"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    Some(!answers.id.isMigrated)
}
