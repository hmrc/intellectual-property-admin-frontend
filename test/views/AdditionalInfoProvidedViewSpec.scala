/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.AdditionalInfoProvidedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AdditionalInfoProvidedView

class AdditionalInfoProvidedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "additionalInfoProvided"

  val form = new AdditionalInfoProvidedFormProvider()()

  "AdditionalInfoProvided view" must {

    val view = injectInstanceOf[AdditionalInfoProvidedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix, routes.AdditionalInfoProvidedController.onSubmit(NormalMode, afaId).url)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
