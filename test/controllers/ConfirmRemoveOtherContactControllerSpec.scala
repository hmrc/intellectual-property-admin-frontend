/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import base.SpecBase
import models.AfaId
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmRemoveOtherContactView

class ConfirmRemoveOtherContactControllerSpec extends SpecBase with LockAfaChecks{

  val afaId: AfaId = userAnswersId
  val contactToRemoveLegal = "legal"
  val contactToRemoveTechnical = "technical"


  def getRequestLegal(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ConfirmRemoveSecondaryLegalContactRoute)

  def getRequestTechnical(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, ConfirmRemoveSecondaryTechnicalContactRoute)

  lazy private val ConfirmRemoveSecondaryLegalContactRoute = routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, contactToRemoveLegal).url
  lazy private val ConfirmRemoveSecondaryTechnicalContactRoute = routes.ConfirmRemoveOtherContactController.onPageLoad(afaId, contactToRemoveTechnical).url


  "Company Applying UK Address Controller" must {

    "return OK and the correct view for a GET on RemoveLegalRoute" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[ConfirmRemoveOtherContactView]

      val result = route(application, getRequestLegal()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(afaId, contactToRemoveLegal)(getRequestLegal(), messages).toString

      application.stop()
    }
    "return OK and the correct view for a GET on RemoveTechnicalRoute" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[ConfirmRemoveOtherContactView]

      val result = route(application, getRequestTechnical()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(afaId, contactToRemoveTechnical)(getRequestTechnical(), messages).toString

      application.stop()
    }
  }
  "redirect to Session Expired for a GET if no existing data is found" in {

    val application = applicationBuilder(userAnswers = None).build()

    val result = route(application, getRequestLegal()).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

    application.stop()
  }

  "for a GET" must {
    redirectIfLocked(afaId, getRequestLegal)
  }
}
