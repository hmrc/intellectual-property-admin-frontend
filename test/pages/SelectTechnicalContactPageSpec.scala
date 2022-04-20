/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import models.ContactOptions
import pages.behaviours.PageBehaviours

class SelectTechnicalContactPageSpec extends PageBehaviours {

  "SelectTechnicalContactPage" must {

    beRetrievable[ContactOptions](SelectTechnicalContactPage)

    beSettable[ContactOptions](SelectTechnicalContactPage)

    beRemovable[ContactOptions](SelectTechnicalContactPage)

    beRequired[ContactOptions](SelectTechnicalContactPage)

  }
}
