/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.CompanyApplying
import play.api.libs.json.JsPath

case object CompanyApplyingPage extends QuestionPage[CompanyApplying] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "companyApplying"
}
