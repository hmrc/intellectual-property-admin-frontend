/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class IpRightsAddNiceClassPageSpec extends PageBehaviours {

  "IpRightsAddGoodsPage" must {

    beRetrievable[Boolean](IpRightsAddNiceClassPage(0))

    beSettable[Boolean](IpRightsAddNiceClassPage(0))

    beRemovable[Boolean](IpRightsAddNiceClassPage(0))
  }
}
