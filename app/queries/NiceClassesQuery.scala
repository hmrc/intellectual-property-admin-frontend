/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import play.api.libs.json.JsPath

final case class NiceClassesQuery(iprIndex: Int) extends Settable[String] {

  override def path: JsPath = JsPath \ "ipRights" \ iprIndex \ "niceClasses"
}
