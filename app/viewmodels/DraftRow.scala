/*
 * Copyright 2022 HM Revenue & Customs
 *
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

    val companyName = company.map {
      c => c.acronym.getOrElse(c.name)
    }

    val publishBy = answers.get(PublicationDeadlineQuery)

    DraftRow(companyName, answers.id, publishBy, isLocked)
  }
}
