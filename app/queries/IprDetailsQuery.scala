/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import models.IprDetails
import play.api.libs.json.JsPath

object IprDetailsQuery extends Gettable[List[IprDetails]] {

  override def path: JsPath = JsPath \ "ipRights"
}
