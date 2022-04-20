/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, NiceClassId, UserAnswers}
import play.api.libs.json.JsPath

final case class IpRightsNiceClassPage(iprIndex: Int, niceClassIndex: Int) extends QuestionPage[NiceClassId] {

  override def path: JsPath = JsPath \ "ipRights" \ iprIndex \ "niceClasses" \ niceClassIndex

  override def toString: String = "niceClass"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    if (niceClassIndex == 0) {
      answers
        .get(IpRightsTypePage(iprIndex))
        .map {
          rightsType =>
            rightsType == IpRightsType.Trademark
        }
    } else {
      Some(false)
    }
}
