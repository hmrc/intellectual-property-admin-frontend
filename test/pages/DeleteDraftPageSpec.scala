/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class DeleteDraftPageSpec extends PageBehaviours {

  "DeleteDraftPage" must {

    beRetrievable[Boolean](DeleteDraftPage)

    beSettable[Boolean](DeleteDraftPage)

    beRemovable[Boolean](DeleteDraftPage)
  }
}
