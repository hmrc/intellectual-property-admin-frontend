/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class EvidenceOfPowerToActPageSpec extends PageBehaviours {

    "EvidenceOfPowerToActPage" must {

      beRetrievable[Boolean](IsExOfficioPage)

      beSettable[Boolean](IsExOfficioPage)

      beRemovable[Boolean](IsExOfficioPage)

      beRequired[Boolean](IsExOfficioPage)

    }
  }
