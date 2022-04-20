/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import play.api.libs.json.JsPath

case object DeleteDraftPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "deleteDraft"
}
