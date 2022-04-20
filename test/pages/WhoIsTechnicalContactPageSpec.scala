/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.TechnicalContact
import pages.behaviours.PageBehaviours

class WhoIsTechnicalContactPageSpec extends PageBehaviours {

  "WhoIsTechnicalContactPage" must {

    beRetrievable[TechnicalContact](WhoIsTechnicalContactPage)

    beSettable[TechnicalContact](WhoIsTechnicalContactPage)

    beRemovable[TechnicalContact](WhoIsTechnicalContactPage)

    beRequired[TechnicalContact](WhoIsTechnicalContactPage)
  }
}
