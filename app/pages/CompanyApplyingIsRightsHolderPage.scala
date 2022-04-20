/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.CompanyApplyingIsRightsHolder
import play.api.libs.json.JsPath

case object CompanyApplyingIsRightsHolderPage extends QuestionPage[CompanyApplyingIsRightsHolder] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "companyApplyingIsRightsHolder"

}
