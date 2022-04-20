/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import play.api.libs.json.{JsObject, JsPath}

case object IpRightsPage extends QuestionPage[List[JsObject]] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "ipRights"
}
