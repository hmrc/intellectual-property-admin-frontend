/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries
import play.api.libs.json.JsPath

object ApplicantLegalContactNameQuery extends Gettable[String] {
  override def path: JsPath = JsPath \ "applicantLegalContact" \ "name"
}
