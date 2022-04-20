/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import play.api.libs.json.JsPath

case object EvidenceProvidedQuery extends Gettable[Boolean] with Settable[Boolean] {

  override def path: JsPath = JsPath \ "representativeContact" \ "evidenceOfPowerToAct"

}
