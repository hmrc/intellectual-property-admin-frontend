/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{ContactOptions, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SelectOtherTechnicalContactPageSpec extends PageBehaviours {

  "SelectOtherTechnicalContactPage" must {

    beRetrievable[ContactOptions](SelectOtherTechnicalContactPage)

    beSettable[ContactOptions](SelectOtherTechnicalContactPage)

    beRemovable[ContactOptions](SelectOtherTechnicalContactPage)


    "be required if add another technical contact is true" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(AddAnotherTechnicalContactPage, true).success.value

          SelectOtherTechnicalContactPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if add another technical contact is false" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .set(AddAnotherTechnicalContactPage, false).success.value

          SelectOtherTechnicalContactPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it's required if we don't know whether to add another technical contact" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers =
            userAnswers
              .remove(AddAnotherTechnicalContactPage).success.value

          SelectOtherTechnicalContactPage.isRequired(answers) must not be defined
      }
    }

  }

}
