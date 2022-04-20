/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate

import base.SpecBase

class DateHintHelperSpec extends SpecBase {

  "DateHintHelper" when {

    "dateInFuture is called" should {
      "give a date three years in the future compared to the given date, with the day fixed to the 7th and month fixed to 5, formatted correctly" in {

        DateHintHelper.dateInFuture(LocalDate.of(2019,5,2)) mustEqual "7 5 2022"

      }
    }

  }
}
