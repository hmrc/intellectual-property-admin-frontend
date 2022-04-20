/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.CompanyApplying
import pages.behaviours.PageBehaviours

class CompanyApplyingPageSpec extends PageBehaviours {

  "CompanyApplyingPage" must {

    beRetrievable[CompanyApplying](CompanyApplyingPage)

    beSettable[CompanyApplying](CompanyApplyingPage)

    beRemovable[CompanyApplying](CompanyApplyingPage)

    beRequired[CompanyApplying](CompanyApplyingPage)
  }
}
