/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import play.api.libs.json.JsPath

final case class IpRightsAddNiceClassPage(iprIndex: Int) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "ipRights" \ iprIndex \ toString

  override def toString: String = "addNiceClass"
}

final case class IpRightsRemoveNiceClassPage(iprIndex: Int) extends Page
