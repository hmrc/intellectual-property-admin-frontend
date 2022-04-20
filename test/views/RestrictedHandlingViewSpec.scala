/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.RestrictedHandlingProvider
import models.{NormalMode, UserAnswers}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.RestrictedHandlingView

class RestrictedHandlingViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "restrictedHandling"

  val form = new RestrictedHandlingProvider()()

  "restrictedHandling view" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(userAnswersId))).build()

    val view = application.injector.instanceOf[RestrictedHandlingView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix, routes.RestrictedHandlingController.onSubmit(NormalMode, afaId).url)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
