/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class RestrictedHandlingPageSpec extends PageBehaviours {

  "RestrictedHandlingPage" must {

    beRetrievable[Boolean](RestrictedHandlingPage)

    beSettable[Boolean](RestrictedHandlingPage)

    beRemovable[Boolean](RestrictedHandlingPage)
  }
}
