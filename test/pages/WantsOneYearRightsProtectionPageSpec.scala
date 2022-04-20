/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class WantsOneYearRightsProtectionPageSpec extends PageBehaviours {

  "WantsOneYearRightsProtectionPage" must {

    beRetrievable[Boolean](WantsOneYearRightsProtectionPage)

    beSettable[Boolean](WantsOneYearRightsProtectionPage)

    beRemovable[Boolean](WantsOneYearRightsProtectionPage)

    "be required if this application is ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsExOfficioPage, true).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if this application is not ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(IsExOfficioPage, false).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers).value mustEqual false
      }
    }

    "not know whether it is required if we do not know if this application is ex-officio" in {

      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.remove(IsExOfficioPage).success.value

          WantsOneYearRightsProtectionPage.isRequired(answers) must not be defined
      }
    }
  }
}
