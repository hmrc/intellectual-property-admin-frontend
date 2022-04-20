/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.NiceClassId
import play.api.libs.json.JsPath

final case class AllIpRightsNiceClasses(iprIndex: Int) extends QuestionPage[List[NiceClassId]] {

  override def path: JsPath = JsPath \ "ipRights" \ iprIndex \ "niceClasses"
}
