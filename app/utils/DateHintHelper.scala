/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHintHelper {

  def dateInFuture(now: LocalDate): String = {

    val fixedDay = 7

    val fixedMonth = 5

    val dateInPast = now.plusYears(3).withDayOfMonth(fixedDay).withMonth(fixedMonth)

    dateInPast.format(DateTimeFormatter.ofPattern("d M yyyy"))

  }

}
