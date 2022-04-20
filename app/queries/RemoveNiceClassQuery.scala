/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package queries

import play.api.libs.json.JsPath

final case class RemoveNiceClassQuery(ipRightsIndex: Int, niceClassIndex: Int) extends Settable[String] {

  override def path: JsPath = JsPath \ "ipRights" \ ipRightsIndex \ "niceClasses" \ niceClassIndex
}
