/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{InternationalAddress, UkAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class IsApplicantLegalContactUkBasedPageSpec extends PageBehaviours {

  "IsApplicantLegalContactUkBasedPage" must {

    beRetrievable[Boolean](IsApplicantLegalContactUkBasedPage)

    beSettable[Boolean](IsApplicantLegalContactUkBasedPage)

    beRemovable[Boolean](IsApplicantLegalContactUkBasedPage)

    beRequired[Boolean](IsApplicantLegalContactUkBasedPage)

    "remove LegalContactUkAddress when IsApplicantLegalContactUkBasedPage is set to false" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[UkAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>

              initial.set(ApplicantLegalContactUkAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsApplicantLegalContactUkBasedPage, false).success.value

          result.get(ApplicantLegalContactUkAddressPage) mustNot be(defined)
      }
    }

    "remove LegalContactInternationalAddress when IsApplicantLegalContactUkBasedPage is set to true" in {

      forAll(arbitrary[UserAnswers], arbitrary[Option[InternationalAddress]]) {
        (initial, answer) =>

          val answers = answer.map {
            address =>
              initial.set(ApplicantLegalContactInternationalAddressPage, address).success.value
          }.getOrElse(initial)

          val result = answers.set(IsApplicantLegalContactUkBasedPage, true).success.value

          result.get(ApplicantLegalContactInternationalAddressPage) mustNot be(defined)
      }
    }
  }
}
