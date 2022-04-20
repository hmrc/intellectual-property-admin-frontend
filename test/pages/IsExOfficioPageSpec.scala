/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsExOfficioPageSpec extends PageBehaviours {

  "IsExOfficioPage" must {

    beRetrievable[Boolean](IsExOfficioPage)

    beSettable[Boolean](IsExOfficioPage)

    beRemovable[Boolean](IsExOfficioPage)

    beRequired[Boolean](IsExOfficioPage)

    "remove WantsOneYearRightsProtection when IsExOfficio is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[Boolean]]) {
        (initial, answer) =>

          val answers = answer.map {
            bool =>
              initial.set(WantsOneYearRightsProtectionPage, bool).success.value
          }.getOrElse(initial)

          val result = answers.set(IsExOfficioPage, false).success.value

          result.get(WantsOneYearRightsProtectionPage) mustNot be (defined)
      }
    }
  }
}
