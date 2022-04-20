/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import models.afa.IpRight
import play.api.libs.json.JsPath

object AllIpRightsQuery extends Gettable[List[IpRight]] with Settable[List[IpRight]]{

 override def path: JsPath = JsPath \ "ipRights"
}
