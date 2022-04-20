/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.ApplicantLegalContactUkAddressFormProvider
import models.{NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.ApplicantLegalContactUkAddressView

class ApplicantLegalContactUkAddressViewSpec extends TextInputViewBehaviours[UkAddress] {

  val messageKeyPrefix = "applicantLegalContactUkAddress"

  val contactName = "foo"

  override val form = new ApplicantLegalContactUkAddressFormProvider()()

  "Applicant Legal Contact UK Address View" must {

    val view = injectInstanceOf[ApplicantLegalContactUkAddressView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, contactName, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      args = Seq(contactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.ApplicantLegalContactUkAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "county", "postCode"),
      titleArgs = Seq(contactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
