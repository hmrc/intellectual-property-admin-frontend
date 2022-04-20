/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class AdditionalInfoProvidedPageSpec extends PageBehaviours {

  "AdditionalInfoProvidedPage" must {

    beRetrievable[Boolean](IsExOfficioPage)

    beSettable[Boolean](IsExOfficioPage)

    beRemovable[Boolean](IsExOfficioPage)

    beRequired[Boolean](IsExOfficioPage)

    "remove RestrictedHandlingPage when AdditionalInfoProvided is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[Boolean]]) {
        (initial, answer) =>

          val answers = answer.map {
            bool =>
              initial.set(RestrictedHandlingPage, bool).success.value
          }.getOrElse(initial)

          val result = answers.set(AdditionalInfoProvidedPage, false).success.value

          result.get(RestrictedHandlingPage) mustNot be (defined)
      }
    }
  }
}
