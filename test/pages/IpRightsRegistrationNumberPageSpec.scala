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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours


class IpRightsRegistrationNumberPageSpec extends PageBehaviours {

  "IpRightsRegistrationNumberPage" must {

    beRetrievable[String](IpRightsRegistrationNumberPage(0))

    beSettable[String](IpRightsRegistrationNumberPage(0))

    beRemovable[String](IpRightsRegistrationNumberPage(0))

    "set and get differently indexed values" in {

      val answersGen =
        Gen.nonEmptyListOf(arbitrary[String])
          .map(_.zipWithIndex)

      forAll(arbitrary[UserAnswers], answersGen) {
        (initialAnswers, extraAnswers) =>

          val answers = extraAnswers.foldLeft(initialAnswers) {
            case (userAnswers, (answer, i)) =>
              userAnswers.set(IpRightsRegistrationNumberPage(i), answer).success.value
          }

          extraAnswers.foreach {
            case (answer, i) =>

              answers.get(IpRightsRegistrationNumberPage(i)).value mustEqual answer
          }
      }
    }

    "be required if this is a Trademark, Design, Patent or Supplementary Protection Certificate" in {

      import models.IpRightsType._

      val rightsTypeGen = Gen.oneOf(Trademark, Design, Patent, SupplementaryProtectionCertificate)

      forAll(arbitrary[UserAnswers], rightsTypeGen) {
        (userAnswers, rightsType) =>

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsRegistrationNumberPage(0).isRequired(answers).value mustEqual true
      }
    }

    "not be required if this is not a Trademark, Design, Patent or Supplementary Protection Certificate" in {

      import models.IpRightsType._

      val rightsTypeGen = Gen.oneOf(PlantVariety, GeographicalIndication, Copyright, SemiconductorTopography)

      forAll(arbitrary[UserAnswers], rightsTypeGen) {
        (userAnswers, rightsType) =>

          val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

          IpRightsRegistrationNumberPage(0).isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we do not know what type of right it is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsRegistrationNumberPage(0).isRequired(answers) must not be defined
      }
    }
  }
}
