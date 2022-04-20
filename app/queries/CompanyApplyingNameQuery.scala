/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries
import play.api.libs.json.JsPath

object CompanyApplyingNameQuery extends Gettable[String] {
  override def path: JsPath = JsPath \ "companyApplying" \ "name"
}
