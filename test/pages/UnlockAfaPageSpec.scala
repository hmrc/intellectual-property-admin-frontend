/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class UnlockAfaPageSpec extends PageBehaviours {

  "UnlockAfaPage" must {

    beRetrievable[Boolean](UnlockAfaPage)

    beSettable[Boolean](UnlockAfaPage)

    beRemovable[Boolean](UnlockAfaPage)
  }
}
