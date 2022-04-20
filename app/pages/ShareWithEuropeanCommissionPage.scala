/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages
import play.api.libs.json.JsPath

case object ShareWithEuropeanCommissionPage extends QuestionPage[Boolean] {
  override def path: JsPath = JsPath \ toString
  override def toString: String = "shareWithEuropeanCommission"
}
