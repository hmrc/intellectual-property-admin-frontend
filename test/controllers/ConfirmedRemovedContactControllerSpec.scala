/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.SpecBase
import models.AfaId
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConfirmedRemovedContactView

class ConfirmedRemovedContactControllerSpec extends SpecBase with LockAfaChecks {

  val afaId: AfaId             = userAnswersId
  val contactToRemoveLegal     = "legal"
  val contactToRemoveTechnical = "technical"

  def getRequestLegal(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, removedLegalContactRoute)

  def getRequestTechnical(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, removedTechnicalContactRoute)

  lazy private val removedLegalContactRoute     =
    routes.ConfirmedRemovedContactController.onPageLoad(afaId, contactToRemoveLegal).url
  lazy private val removedTechnicalContactRoute =
    routes.ConfirmedRemovedContactController.onPageLoad(afaId, contactToRemoveTechnical).url

  "Company Applying UK Address Controller" must {

    "return OK and the correct view for a GET on removedLegalContactRoute" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[ConfirmedRemovedContactView]

      val result = route(application, getRequestLegal()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(afaId, contactToRemoveLegal)(messages).toString

      application.stop()
    }
    "return OK and the correct view for a GET on RemoveTechnicalRoute" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val view = application.injector.instanceOf[ConfirmedRemovedContactView]

      val result = route(application, getRequestTechnical()).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(afaId, contactToRemoveTechnical)(messages).toString

      application.stop()
    }
  }
  "redirect to Session Expired for a GET if no existing data is found" in {

    val application = applicationBuilder(userAnswers = None).build()

    val result = route(application, getRequestLegal()).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url

    application.stop()
  }

  "for a GET" must {
    redirectIfLocked(afaId, getRequestLegal)
  }
}
