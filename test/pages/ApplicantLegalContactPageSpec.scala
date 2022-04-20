/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.ApplicantLegalContact
import pages.behaviours.PageBehaviours

class ApplicantLegalContactPageSpec extends PageBehaviours {

  "ApplicantLegalContactPage" must {

    beRetrievable[ApplicantLegalContact](ApplicantLegalContactPage)

    beSettable[ApplicantLegalContact](ApplicantLegalContactPage)

    beRemovable[ApplicantLegalContact](ApplicantLegalContactPage)

    beRequired[ApplicantLegalContact](ApplicantLegalContactPage)

  }
}
