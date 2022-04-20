/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import play.api.libs.json.JsPath

final case class DeleteNiceClassPage(iprIndex: Int, niceCLassIndex: Int) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "deleteNiceClass"
}
