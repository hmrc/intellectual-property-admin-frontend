/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import models.IpRight
import play.api.libs.json.JsPath

object IpRightQuery extends Gettable[List[IpRight]] {

  override def path: JsPath = JsPath \ "ipRights"
}
