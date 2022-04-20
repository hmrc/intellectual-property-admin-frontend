/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class RepresentativeContactInternationalAddressPageSpec extends PageBehaviours {

  "RepresentativeContactInternationalAddressPage" must {

    beRetrievable[InternationalAddress](RepresentativeContactInternationalAddressPage)

    beSettable[InternationalAddress](RepresentativeContactInternationalAddressPage)

    beRemovable[InternationalAddress](RepresentativeContactInternationalAddressPage)

    "be required if the Representative contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, false).success.value

          RepresentativeContactInternationalAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the Representative contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, true).success.value

          RepresentativeContactInternationalAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we do not know whether the Representative contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IsRepresentativeContactUkBasedPage).success.value

          RepresentativeContactInternationalAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
