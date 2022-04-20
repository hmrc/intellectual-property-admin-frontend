/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsApplicantSecondaryLegalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsApplicantSecondaryLegalContactUkBasedView

class IsApplicantSecondaryLegalContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix: String = "isApplicantSecondaryLegalContactUkBased"

  val secondaryLegalContactName: String = "foo"

  val form = new IsApplicantSecondaryLegalContactUkBasedFormProvider()(secondaryLegalContactName)

  "IsApplicantSecondaryLegalContactUkBased view" must {

    val view = injectInstanceOf[IsApplicantSecondaryLegalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, secondaryLegalContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(secondaryLegalContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(form, applyView, messageKeyPrefix,
      routes.IsApplicantSecondaryLegalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(secondaryLegalContactName), argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
