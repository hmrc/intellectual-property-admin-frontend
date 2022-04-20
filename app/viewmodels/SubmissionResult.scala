/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package viewmodels

import models.AfaId

final case class SubmissionResult(afaId: AfaId, applicantCompanyName: String, expirationDate: String)
