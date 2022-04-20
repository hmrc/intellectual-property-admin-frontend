/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class DeleteNiceClassPageSpec extends PageBehaviours {

  "DeleteNiceClassPage" must {

    beRetrievable[Boolean](DeleteNiceClassPage(0, 0))

    beSettable[Boolean](DeleteNiceClassPage(0, 0))

    beRemovable[Boolean](DeleteNiceClassPage(0, 0))
  }
}
