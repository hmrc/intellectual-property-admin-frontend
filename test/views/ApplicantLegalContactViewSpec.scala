/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.ApplicantLegalContactFormProvider
import generators.Generators
import models.{ApplicantLegalContact, NormalMode, UserAnswers}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.ApplicantLegalContactView

class ApplicantLegalContactViewSpec extends TextInputViewBehaviours[ApplicantLegalContact] with Generators {

  val messageKeyPrefix = "applicantLegalContact"
  val applicantName: String = nonEmptyString.sample.value

  override val form = new ApplicantLegalContactFormProvider()()

  "ApplicantLegalContactView" must {

    val view = injectInstanceOf[ApplicantLegalContactView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)

    val renderedView: HtmlFormat.Appendable = view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(form),
      messageKeyPrefix,
      Seq(applicantName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.ApplicantLegalContactController.onSubmit(NormalMode, afaId).url,
      Seq("name", "companyName", "telephone", "otherTelephone", "email"),
      titleArgs = Seq(applicantName),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    implicit val doc: Document = asDocument(renderedView)

    "display the correct contact name field hint" in {
      element("#name-hint").text() mustBe "For example, Jane Smith"
    }

    "display the correct telephone number field hint" in {
      element("#telephone-hint").text() mustBe "For international numbers include the country code"
    }
  }
}
