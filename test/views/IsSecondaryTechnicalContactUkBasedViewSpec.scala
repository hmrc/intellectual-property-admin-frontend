/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsSecondaryTechnicalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsSecondaryTechnicalContactUkBasedView

class IsSecondaryTechnicalContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix: String = "isSecondaryTechnicalContactUkBased"

  val secondaryTechnicalContactName: String = "foo"

  val form = new IsSecondaryTechnicalContactUkBasedFormProvider()(secondaryTechnicalContactName)

  "IsSecondaryTechnicalContactUkBased view" must {

    val view = injectInstanceOf[IsSecondaryTechnicalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, secondaryTechnicalContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(secondaryTechnicalContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form, applyView,
      messageKeyPrefix,
      routes.IsSecondaryTechnicalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(secondaryTechnicalContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
