/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{IpRightsType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours


class IpRightsDescriptionPageSpec extends PageBehaviours {

  "IpRightsDescriptionPage" must {

    beRetrievable[String](IpRightsDescriptionPage(0))

    beSettable[String](IpRightsDescriptionPage(0))

    beRemovable[String](IpRightsDescriptionPage(0))

    "be required if this right is not a Trademark" in {

      forAll(arbitrary[UserAnswers], arbitrary[IpRightsType]) {
        (userAnswers, rightsType) =>

          whenever(rightsType != IpRightsType.Trademark) {

            val answers = userAnswers.set(IpRightsTypePage(0), rightsType).success.value

            IpRightsDescriptionPage(0).isRequired(answers).value mustEqual true
          }
      }
    }

    "not be required if this right is a Trademark" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(IpRightsTypePage(0), IpRightsType.Trademark).success.value

          IpRightsDescriptionPage(0).isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know what type of right this is" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IpRightsTypePage(0)).success.value

          IpRightsDescriptionPage(0).isRequired(answers) must not be defined
      }
    }
  }
}
