/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.CompanyApplyingInternationalAddressFormProvider
import models.{InternationalAddress, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.CompanyApplyingInternationalAddressView

class CompanyApplyingInternationalAddressViewSpec extends TextInputViewBehaviours[InternationalAddress] {

  val messageKeyPrefix = "companyApplyingInternationalAddress"

  val contactName = "foo"

  override val form = new CompanyApplyingInternationalAddressFormProvider()()

  "companyApplyingInternationalAddress" must {

    val view = injectInstanceOf[CompanyApplyingInternationalAddressView](Some(emptyUserAnswers))

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

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.CompanyApplyingInternationalAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "country", "postCode"),
      titleArgs = Seq(contactName),
      argsUsedInBrowserTitle = true
    )
  }
}
