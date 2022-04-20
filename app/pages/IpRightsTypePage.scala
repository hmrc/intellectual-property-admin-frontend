/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, UserAnswers}
import play.api.libs.json.JsPath
import queries.NiceClassesQuery

import scala.util.Try

final case class IpRightsTypePage(index: Int) extends QuestionPage[IpRightsType] {

  override def path: JsPath = JsPath \ "ipRights" \ index \ toString

  override def toString: String = "rightsType"

  override def cleanup(value: Option[IpRightsType], userAnswers: UserAnswers): Try[UserAnswers] = {

    import IpRightsType._

    value match {
      case Some(Trademark) =>
        userAnswers
          .remove(IpRightsSupplementaryProtectionCertificateTypePage(index))

      case Some(Design) | Some(Patent) =>
        userAnswers
          .remove(IpRightsDescriptionWithBrandPage(index))
          .flatMap(_.remove(NiceClassesQuery(index)))
          .flatMap(_.remove(IpRightsSupplementaryProtectionCertificateTypePage(index)))

      case Some(SupplementaryProtectionCertificate) =>
        userAnswers
          .remove(IpRightsDescriptionWithBrandPage(index))
          .flatMap(_.remove(NiceClassesQuery(index)))

      case Some(GeographicalIndication) =>
        userAnswers
          .remove(IpRightsRegistrationNumberPage(index))
          .flatMap(_.remove(IpRightsRegistrationEndPage(index )))
          .flatMap(_.remove(IpRightsDescriptionWithBrandPage(index)))
          .flatMap(_.remove(NiceClassesQuery(index)))
          .flatMap(_.remove(IpRightsSupplementaryProtectionCertificateTypePage(index)))

      case Some(_) =>
        userAnswers
          .remove(IpRightsRegistrationNumberPage(index))
          .flatMap(_.remove(IpRightsRegistrationEndPage(index)))
          .flatMap(_.remove(IpRightsDescriptionWithBrandPage(index)))
          .flatMap(_.remove(NiceClassesQuery(index)))
          .flatMap(_.remove(IpRightsSupplementaryProtectionCertificateTypePage(index)))

      case None =>
        super.cleanup(value, userAnswers)
    }
  }

  override def isRequired(answers: UserAnswers): Option[Boolean] =
    Some(index == 0)
}
