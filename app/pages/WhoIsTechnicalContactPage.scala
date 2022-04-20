/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.TechnicalContact
import play.api.libs.json.JsPath

case object WhoIsTechnicalContactPage extends QuestionPage[TechnicalContact] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whoIsTechnicalContact"

}
