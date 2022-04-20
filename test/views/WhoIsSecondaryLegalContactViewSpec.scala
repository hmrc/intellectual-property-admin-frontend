/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.WhoIsSecondaryLegalContactFormProvider
import models.{NormalMode, UserAnswers, WhoIsSecondaryLegalContact}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.WhoIsSecondaryLegalContactView

class WhoIsSecondaryLegalContactViewSpec extends TextInputViewBehaviours[WhoIsSecondaryLegalContact] {

  val messageKeyPrefix = "whoIsSecondaryLegalContact"

  override val form = new WhoIsSecondaryLegalContactFormProvider()()

  "WhoIsSecondaryLegalContactView" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[WhoIsSecondaryLegalContactView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, "applicantName")(fakeRequest, messages)

    val renderedView: HtmlFormat.Appendable = view.apply(form, NormalMode, afaId, "applicantName")(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, Seq("applicantName"))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.WhoIsSecondaryLegalContactController.onSubmit(NormalMode, afaId).url,
      Seq("name", "companyName", "telephone", "email"),
      titleArgs = Seq("applicantName"),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    implicit val doc: Document = asDocument(renderedView)

    "display the correct contact name field hint" in {
      element("#name-hint").text() mustBe "For example, Jane Smith"
    }

    "display the correct telephone number field hint" in {
      element("#telephone-hint").text() mustBe "For international numbers include the country code"
    }
  }
}
