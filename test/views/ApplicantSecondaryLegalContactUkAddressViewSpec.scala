/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.ApplicantSecondaryLegalContactUkAddressFormProvider
import models.{NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.ApplicantSecondaryLegalContactUkAddressView

class ApplicantSecondaryLegalContactUkAddressViewSpec extends TextInputViewBehaviours[UkAddress] {

  val messageKeyPrefix: String = "applicantSecondaryLegalContactUkAddress"

  val secondaryLegalContactName: String = "foo"

  override val form = new ApplicantSecondaryLegalContactUkAddressFormProvider()()

  "Applicant Secondary Legal Contact UK Address View" must {

    val view = injectInstanceOf[ApplicantSecondaryLegalContactUkAddressView](Some(emptyUserAnswers))

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

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.ApplicantSecondaryLegalContactUkAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "county", "postCode"), titleArgs = Seq(secondaryLegalContactName), argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
