/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsTechnicalContactUkBasedPageSpec extends PageBehaviours {

  "IsTechnicalContactUkBasedPage" must {

    beRetrievable[Boolean](IsTechnicalContactUkBasedPage)

    beSettable[Boolean](IsTechnicalContactUkBasedPage)

    beRemovable[Boolean](IsTechnicalContactUkBasedPage)

    beRequired[Boolean](IsTechnicalContactUkBasedPage)

    "remove TechnicalContactUkAddress when IsTechnicalContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(TechnicalContactUkAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsTechnicalContactUkBasedPage, false).success.value

          result.get(TechnicalContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove TechnicalContactInternationalAddress when IsInfringementsContactUkBased is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(TechnicalContactInternationalAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsTechnicalContactUkBasedPage, true).success.value

          result.get(TechnicalContactInternationalAddressPage) mustNot be(defined)
      }
    }
  }
}
