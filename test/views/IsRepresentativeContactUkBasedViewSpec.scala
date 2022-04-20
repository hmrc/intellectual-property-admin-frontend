/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.IsRepresentativeContactUkBasedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IsRepresentativeContactUkBasedView

class IsRepresentativeContactUkBasedViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "isRepresentativeContactUkBased"

  val rightsHolderContactName = "foo"

  val form = new IsRepresentativeContactUkBasedFormProvider()(rightsHolderContactName)

  "isRepresentativeContactUkBased view" must {

    val view = injectInstanceOf[IsRepresentativeContactUkBasedView](Some(emptyUserAnswers))

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
      routes.IsRepresentativeContactUkBasedController.onSubmit(NormalMode, afaId).url,
      args = Seq(rightsHolderContactName),
      argsUsedInBrowserTitle = true)

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

  }
}
