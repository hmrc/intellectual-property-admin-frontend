/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries
import models.NiceClassId
import play.api.libs.json.JsPath

final case class NiceClassIdsQuery(index: Int) extends Gettable[List[NiceClassId]] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ "niceClasses"
}
