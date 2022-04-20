/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.{CompanyApplyingIsRightsHolder}
import pages.behaviours.PageBehaviours

class CompanyApplyingIsRightsHolderPageSpec extends PageBehaviours {

  "CompanyApplyingIsRightsHolderPage" must {

    beRetrievable[CompanyApplyingIsRightsHolder](CompanyApplyingIsRightsHolderPage)

    beSettable[CompanyApplyingIsRightsHolder](CompanyApplyingIsRightsHolderPage)

    beRemovable[CompanyApplyingIsRightsHolder](CompanyApplyingIsRightsHolderPage)

    beRequired[CompanyApplyingIsRightsHolder](CompanyApplyingIsRightsHolderPage)
  }
}
