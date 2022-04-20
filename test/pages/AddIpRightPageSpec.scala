/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class AddIpRightPageSpec extends PageBehaviours {

  "AddIpRightPage" must {

    beRetrievable[Boolean](AddIpRightPage)

    beSettable[Boolean](AddIpRightPage)

    beRemovable[Boolean](AddIpRightPage)
  }
}
