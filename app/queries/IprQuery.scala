/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import models.afa.IpRights
import play.api.libs.json.JsPath

object IprQuery extends Gettable[List[IpRights]] {

  override def path: JsPath = JsPath \ "ipRights"
}
