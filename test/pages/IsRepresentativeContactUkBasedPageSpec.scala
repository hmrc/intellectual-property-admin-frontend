/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsRepresentativeContactUkBasedPageSpec extends PageBehaviours {

  "IsRepresentativeContactUkBasedPage" must {

    beRetrievable[Boolean](IsRepresentativeContactUkBasedPage)

    beSettable[Boolean](IsRepresentativeContactUkBasedPage)

    beRemovable[Boolean](IsRepresentativeContactUkBasedPage)

    beRequired[Boolean](IsRepresentativeContactUkBasedPage)

    "remove RepresentativeContactUkAddress when IsRepresentativeContactUkBased is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(RepresentativeContactUkAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsRepresentativeContactUkBasedPage, false).success.value

          result.get(RepresentativeContactUkAddressPage) mustNot be (defined)
      }
    }

    "remove RepresentativeContactInternationalAddress when IsRepresentativeContactUkBased is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(RepresentativeContactInternationalAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsRepresentativeContactUkBasedPage, true).success.value

          result.get(RepresentativeContactInternationalAddressPage) mustNot be (defined)
      }
    }
  }
}
