/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import play.api.libs.json.JsPath

final case class IpRightsRegistrationNumberPage(index: Int) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "registrationNumber"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {

    import models.IpRightsType._

    answers.get(IpRightsTypePage(index)).map {
      rightsType =>

        val relevantRightsTypes = Seq(Trademark, Design, Patent, SupplementaryProtectionCertificate)
        relevantRightsTypes.contains(rightsType)
    }
  }
}
