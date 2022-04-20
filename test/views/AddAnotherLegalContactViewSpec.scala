/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.AddAnotherLegalContactFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AddAnotherLegalContactView

class AddAnotherLegalContactViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addAnotherLegalContact"

  val form = new AddAnotherLegalContactFormProvider()()

  "AddAnotherLegalContact view" must {

    val view = injectInstanceOf[AddAnotherLegalContactView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix, routes.AddAnotherLegalContactController.onSubmit(NormalMode, afaId).url)

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
