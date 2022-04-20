/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.UserAnswers
import play.api.libs.json.JsPath

final case class IpRightsRegistrationEndPage(index: Int) extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "registrationEnd"

  override def isRequired(answers: UserAnswers): Option[Boolean] = {

    import models.IpRightsType._

    answers.get(IpRightsTypePage(index)).map {
      rightsType =>

        val relevantRightsTypes = Seq(Trademark, Design, Patent, SupplementaryProtectionCertificate)
        relevantRightsTypes.contains(rightsType)
    }
  }
}
