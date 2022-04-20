/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{UserAnswers, TechnicalContact}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class WhoIsSecondaryTechnicalContactPageSpec extends PageBehaviours {

  "WhoIsSecondaryTechnicalContactPage" must {

    beRetrievable[TechnicalContact]

    beSettable[TechnicalContact]

    beRemovable[TechnicalContact]

    "be required if add another technical contact is true" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnotherTechnicalContactPage, true).success.value

          WhoIsSecondaryTechnicalContactPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if add another technical contact is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddAnotherTechnicalContactPage, false).success.value

          WhoIsSecondaryTechnicalContactPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know whether to add another technical contact" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(AddAnotherTechnicalContactPage).success.value

          WhoIsSecondaryTechnicalContactPage.isRequired(answers) must not be defined
      }
    }
  }
}
