/*
 * Copyright 2023 HM Revenue & Customs
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

package generators

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(IpRightsSupplementaryProtectionCertificateTypePage, JsValue)] ::
      arbitrary[(IpRightsNiceClassPage, JsValue)] ::
      arbitrary[(IpRightsDescriptionWithBrandPage, JsValue)] ::
      arbitrary[(IpRightsDescriptionPage, JsValue)] ::
      arbitrary[(WhoIsTechnicalContactPage.type, JsValue)] ::
      arbitrary[(RepresentativeDetailsPage.type, JsValue)] ::
      arbitrary[(EvidenceOfPowerToActPage.type, JsValue)] ::
      arbitrary[(IsRepresentativeContactLegalContactPage.type, JsValue)] ::
      arbitrary[(ApplicantLegalContactPage.type, JsValue)] ::
      arbitrary[(CompanyApplyingIsRightsHolderPage.type, JsValue)] ::
      arbitrary[(CompanyApplyingPage.type, JsValue)] ::
      arbitrary[(DeleteDraftPage.type, JsValue)] ::
      arbitrary[(UnlockAfaPage.type, JsValue)] ::
      arbitrary[(AddIpRightPage.type, JsValue)] ::
      arbitrary[(IpRightsAddNiceClassPage, JsValue)] ::
      arbitrary[(IpRightsRegistrationEndPage, JsValue)] ::
      arbitrary[(IpRightsRegistrationNumberPage, JsValue)] ::
      arbitrary[(PermissionToDestroySmallConsignmentsPage.type, JsValue)] ::
      arbitrary[(WantsOneYearRightsProtectionPage.type, JsValue)] ::
      arbitrary[(IpRightsTypePage, JsValue)] ::
      arbitrary[(IsExOfficioPage.type, JsValue)] ::
      arbitrary[(TechnicalContactInternationalAddressPage.type, JsValue)] ::
      arbitrary[(ApplicantLegalContactInternationalAddressPage.type, JsValue)] ::
      arbitrary[(TechnicalContactUkAddressPage.type, JsValue)] ::
      arbitrary[(ApplicantLegalContactUkAddressPage.type, JsValue)] ::
      arbitrary[(IsApplicantLegalContactUkBasedPage.type, JsValue)] ::
      arbitrary[(IsTechnicalContactUkBasedPage.type, JsValue)] ::
      arbitrary[(ApplicationReceiptDatePage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id   <- arbitrary[AfaId]
        data <- generators match {
                  case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                  case _   => Gen.mapOf(oneOf(generators))
                }
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        }
      )
    }
  }
}
