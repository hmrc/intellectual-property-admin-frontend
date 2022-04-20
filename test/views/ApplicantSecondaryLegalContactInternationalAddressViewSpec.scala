/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.ApplicantSecondaryLegalContactInternationalAddressFormProvider
import models.{InternationalAddress, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.ApplicantSecondaryLegalContactInternationalAddressView

class ApplicantSecondaryLegalContactInternationalAddressViewSpec extends TextInputViewBehaviours[InternationalAddress] {

  val messageKeyPrefix = "applicantSecondaryLegalContactInternationalAddress"

  val secondaryLegalContactName = "foo"

  override val form = new ApplicantSecondaryLegalContactInternationalAddressFormProvider()()

  "ApplicantSecondaryLegalContactInternationalAddressView" must {

    val view = injectInstanceOf[ApplicantSecondaryLegalContactInternationalAddressView](Some(emptyUserAnswers))

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
      routes.ApplicantSecondaryLegalContactInternationalAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "country", "postCode"), titleArgs = Seq(secondaryLegalContactName), argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
