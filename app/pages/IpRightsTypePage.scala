/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
          .flatMap(_.remove(IpRightsRegistrationEndPage(index)))
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
