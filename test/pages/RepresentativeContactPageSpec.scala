/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package pages


import models.RepresentativeDetails
import pages.behaviours.PageBehaviours

class RepresentativeContactPageSpec extends PageBehaviours {

  "RepresentativeDetailsPage" must {

    beRetrievable[RepresentativeDetails](RepresentativeDetailsPage)

    beSettable[RepresentativeDetails](RepresentativeDetailsPage)

    beRemovable[RepresentativeDetails](RepresentativeDetailsPage)

    beRequired[RepresentativeDetails](RepresentativeDetailsPage)
  }
}
