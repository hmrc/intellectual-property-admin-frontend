/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import java.time.LocalDate

import models.UserAnswers
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ApplicationReceiptDatePageSpec extends PageBehaviours {

  "ApplicationReceiptDatePage" must {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2018, 1, 1))
    }

    beRetrievable[LocalDate](ApplicationReceiptDatePage)

    beSettable[LocalDate](ApplicationReceiptDatePage)

    beRemovable[LocalDate](ApplicationReceiptDatePage)

    "be required if this application is not migrated" in {

      forAll(arbitrary[UserAnswers], arbitraryUkAfaId(None)) {
        (userAnswers, afaId) =>

          val answers = userAnswers.copy(id = afaId)

          ApplicationReceiptDatePage.isRequired(answers).value mustEqual true
      }
    }

    "not be required if this application is migrated" in {

      forAll(arbitrary[UserAnswers], arbitraryGbAfaId(None)) {
        (userAnswers, afaId) =>

          val answers = userAnswers.copy(id = afaId)

          ApplicationReceiptDatePage.isRequired(answers).value mustEqual false
      }
    }
  }
}
