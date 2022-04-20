/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsSupplementaryProtectionCertificateType, IpRightsType, UserAnswers}
import play.api.libs.json.JsPath

final case class IpRightsSupplementaryProtectionCertificateTypePage(index: Int) extends QuestionPage[IpRightsSupplementaryProtectionCertificateType] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "certificateType"

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    answers
      .get(IpRightsTypePage(index))
      .map {
        rightsType =>
          rightsType == IpRightsType.SupplementaryProtectionCertificate
      }
}
