/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, NiceClassId, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class IpRightsNiceClassPageSpec extends PageBehaviours {

  "IpRightsNiceClassPage" must {

    beRetrievable[NiceClassId](IpRightsNiceClassPage(0, 0))

    beSettable[NiceClassId](IpRightsNiceClassPage(0, 0))

    beRemovable[NiceClassId](IpRightsNiceClassPage(0, 0))

    "be required for index 0 if this right is a Trademark" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

          IpRightsNiceClassPage(0, 0).isRequired(answers).value mustEqual true
      }
    }

    "not be required for index 0 if this right is not a Trademark" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.Trademark) {

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            IpRightsNiceClassPage(0, 0).isRequired(answers).value mustEqual false
          }
      }
    }

    "not be required for indexes above 0" in {

      val arbitrarilyHighIndex = 1000

      forAll(arbitrary[UserAnswers], Gen.choose(1, arbitrarilyHighIndex)) {
        (userAnswers, index) =>

          IpRightsNiceClassPage(0, index).isRequired(userAnswers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsNiceClassPage(0, 0).isRequired(answers) must not be defined
      }
    }
  }
}
