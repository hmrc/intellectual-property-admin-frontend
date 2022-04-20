/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
