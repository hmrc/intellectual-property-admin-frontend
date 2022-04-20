/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class RepresentativeContactUkAddressPageSpec extends PageBehaviours {

  "RepresentativeContactUkAddressPage" must {

    beRetrievable[UkAddress](RepresentativeContactUkAddressPage)

    beSettable[UkAddress](RepresentativeContactUkAddressPage)

    beRemovable[UkAddress](RepresentativeContactUkAddressPage)

    "be required if the Representative Contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, true).success.value

          RepresentativeContactUkAddressPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if the Representative Contact is not based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsRepresentativeContactUkBasedPage, false).success.value

          RepresentativeContactUkAddressPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know whether the Representative contact is based in the UK" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IsRepresentativeContactUkBasedPage).success.value

          RepresentativeContactUkAddressPage.isRequired(answers) must not be defined
      }
    }
  }
}
