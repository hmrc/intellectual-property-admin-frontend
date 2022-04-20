/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import play.api.libs.json.JsPath

case object EvidenceOfPowerToActPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "representativeContact" \ toString

  override def toString: String = "evidenceOfPowerToAct"
}
