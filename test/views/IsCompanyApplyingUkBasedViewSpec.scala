/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsCompanyApplyingUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsCompanyApplyingUkBasedView

class IsCompanyApplyingUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isCompanyApplyingUkBased"

  val rightsHolderContactName = "foo"

  val form = new IsCompanyApplyingUkBasedFormProvider()(rightsHolderContactName)

  "isCompanyApplyingUkBased view" must {

    val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()
    val view = application.injector.instanceOf[IsCompanyApplyingUkBasedView]

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
      routes.IsCompanyApplyingUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
