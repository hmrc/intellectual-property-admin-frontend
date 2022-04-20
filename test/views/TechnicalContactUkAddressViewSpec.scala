/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.TechnicalContactUkAddressFormProvider
import models.{NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.TechnicalContactUkAddressView

class TechnicalContactUkAddressViewSpec extends TextInputViewBehaviours[UkAddress] {

  val messageKeyPrefix = "technicalContactUkAddress"

  val contactName = "foo"

  override val form = new TechnicalContactUkAddressFormProvider()()

  "TechnicalContactUkAddressView" must {

    val view = injectInstanceOf[TechnicalContactUkAddressView](Some(emptyUserAnswers))

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
      routes.TechnicalContactUkAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "county", "postCode"),
      titleArgs = Seq(contactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
