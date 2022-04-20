/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate

import base.SpecBase
import models.{IpRightsType, ModifyMode, NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{OptionValues, TryValues}
import pages._
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckIprDetailsView

class CheckIprDetailsControllerSpec extends SpecBase with IprIndexValidation with TryValues with OptionValues {

  "Check Ipr Details Controller" must {

    val index = 0

    val afaId = userAnswersId

    val onwardRoute = Call("GET", "foo")

    "return OK and the correct view for a GET" in {

      val userAnswers =
        UserAnswers(afaId)
          .set(IpRightsTypePage(index), IpRightsType.Design).success.value
          .set(IpRightsRegistrationNumberPage(index), "1234567890").success.value
          .set(IpRightsRegistrationEndPage(index), LocalDate.now).success.value
          .set(IpRightsDescriptionPage(index), "description").success.value

      val cyaHelper = new CheckYourAnswersHelper(userAnswers)

      val expectedMainSection =
        AnswerSection(
          None,
          Seq(
            cyaHelper.ipRightsType(ModifyMode, index).value,
            cyaHelper.ipRightsRegistrationNumber(ModifyMode, index).value,
            cyaHelper.ipRightsRegistrationEnd(ModifyMode, index).value,
            cyaHelper.ipRightsDescription(ModifyMode, index).value
          )
        )

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request = FakeRequest(GET, routes.CheckIprDetailsController.onPageLoad(NormalMode, index, afaId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CheckIprDetailsView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(NormalMode, afaId, index, expectedMainSection, None, onwardRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, routes.CheckIprDetailsController.onPageLoad(NormalMode, index, afaId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    def getForIndex(index: Int): FakeRequest[AnyContentAsEmpty.type] = {
      val route = routes.CheckIprDetailsController.onPageLoad(NormalMode, index, afaId).url

      FakeRequest(GET, route)
    }

    validateOnIprIndex(
      arbitrary[IpRightsType],
      IpRightsTypePage.apply,
      getForIndex
    )
  }
}
