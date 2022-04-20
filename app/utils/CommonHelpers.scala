/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package utils

import models.requests.DataRequest
import pages.CompanyApplyingPage
import play.api.i18n.Messages
import play.api.mvc.AnyContent

object CommonHelpers{

  def getApplicantName[A](block: String => A)
                                 (implicit request: DataRequest[AnyContent], messages: Messages): A = {
    block {
      request.userAnswers
        .get(CompanyApplyingPage)
        .map(company => company.acronym.getOrElse(company.name))
        .fold(Messages("companyApplying.unknownApplicant"))(identity)
    }
  }
}

