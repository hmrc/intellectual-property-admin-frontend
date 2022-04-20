/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages

import pages.behaviours.PageBehaviours

class PermissionToDestroySmallConsignmentsPageSpec extends PageBehaviours {

  "PermissionToDestroySmallConsignmentsPage" must {

    beRetrievable[Boolean](PermissionToDestroySmallConsignmentsPage)

    beSettable[Boolean](PermissionToDestroySmallConsignmentsPage)

    beRemovable[Boolean](PermissionToDestroySmallConsignmentsPage)

    beRequired[Boolean](PermissionToDestroySmallConsignmentsPage)
  }
}
