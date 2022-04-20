/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsApplicantLegalContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsApplicantLegalContactUkBasedView

class IsApplicantLegalContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isApplicantLegalContactUkBased"

  val rightsHolderContactName = "foo"

  val form = new IsApplicantLegalContactUkBasedFormProvider()(rightsHolderContactName)

  "IsApplicantLegalContactUkBased view" must {

    val view = injectInstanceOf[IsApplicantLegalContactUkBasedView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, rightsHolderContactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPageUsingDesignSystem(
      form,
      applyView,
      messageKeyPrefix,
      routes.IsApplicantLegalContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
