/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class WhoIsSecondaryLegalContactPageSpec extends PageBehaviours {

  "WhoIsSecondaryLegalContactPageSpec" must {

    beRetrievable[WhoIsSecondaryLegalContactPageSpec]

    beSettable[WhoIsSecondaryLegalContactPageSpec]

    beRemovable[WhoIsSecondaryLegalContactPageSpec]

    "be required if add another legal contact is true" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnotherLegalContactPage, true).success.value

          WhoIsSecondaryLegalContactPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if add another legal contact is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnotherLegalContactPage, false).success.value

          WhoIsSecondaryLegalContactPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know whether to add another legal contact" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(AddAnotherLegalContactPage).success.value

          WhoIsSecondaryLegalContactPage.isRequired(answers) must not be defined
      }
    }
  }
}
