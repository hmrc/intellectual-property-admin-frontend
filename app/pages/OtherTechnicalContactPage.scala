/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.afa.Contact
import play.api.libs.json.JsPath

case object OtherTechnicalContactPage extends QuestionPage[Contact] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "otherTechnicalContact"



}
