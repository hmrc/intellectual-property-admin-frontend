/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.AddAnotherTechnicalContactFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.AddAnotherTechnicalContactView

class AddAnotherTechnicalContactViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "addAnotherTechnicalContact"

  val form = new AddAnotherTechnicalContactFormProvider()()

  "AddAnotherTechnicalContact view" must {

    val view = injectInstanceOf[AddAnotherTechnicalContactView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix, routes.AddAnotherTechnicalContactController.onSubmit(NormalMode, afaId).url)

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }

}
