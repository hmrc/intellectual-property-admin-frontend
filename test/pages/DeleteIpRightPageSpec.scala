/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class DeleteIpRightPageSpec extends PageBehaviours {

  "DeleteIpRightPage" must {

    beRetrievable[Boolean](DeleteIpRightPage(0))

    beSettable[Boolean](DeleteIpRightPage(0))

    beRemovable[Boolean](DeleteIpRightPage(0))
  }
}
