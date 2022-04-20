/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.ContactOptions
import play.api.libs.json.JsPath

case object SelectTechnicalContactPage extends QuestionPage[ContactOptions] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "selectTechnicalContact"

}
