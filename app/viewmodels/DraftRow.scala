/*
 * Copyright 2023 HM Revenue & Customs
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

package viewmodels

import java.time.LocalDate

import models.{AfaId, UserAnswers}
import pages.CompanyApplyingPage
import queries.PublicationDeadlineQuery

final case class DraftRow(
  companyName: Option[String],
  reference: AfaId,
  publishBy: Option[LocalDate],
  isLocked: Boolean
)

object DraftRow {

  def apply(answers: UserAnswers, isLocked: Boolean): DraftRow = {

    val company = answers.get(CompanyApplyingPage)

    val companyName = company.map { c =>
      c.acronym.getOrElse(c.name)
    }

    val publishBy = answers.get(PublicationDeadlineQuery)

    DraftRow(companyName, answers.id, publishBy, isLocked)
  }
}
