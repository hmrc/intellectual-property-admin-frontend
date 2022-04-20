/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.RepresentativeContactUkAddressFormProvider
import models.{NormalMode, UkAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.RepresentativeContactUkAddressView

class RepresentativeContactUkAddressViewSpec extends TextInputViewBehaviours[UkAddress] {

  val messageKeyPrefix = "representativeContactUkAddress"

  val contactName = "foo"

  override val form = new RepresentativeContactUkAddressFormProvider()()

  "representativeContactUkAddress" must {

    val view = injectInstanceOf[RepresentativeContactUkAddressView](Some(emptyUserAnswers))

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
      routes.RepresentativeContactUkAddressController.onSubmit(NormalMode, afaId).url,
      Seq("line1", "line2", "town", "county", "postCode"),
      titleArgs = Seq(contactName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))
  }
}
