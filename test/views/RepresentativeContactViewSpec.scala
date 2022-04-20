/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.RepresentativeContactFormProvider
import generators.Generators
import models.{NormalMode, RepresentativeDetails, UserAnswers}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.RepresentativeContactView


class RepresentativeContactViewSpec extends TextInputViewBehaviours[RepresentativeDetails] with Generators {

  val messageKeyPrefix = "representativeContact"
  val applicantName = nonEmptyString.sample.value

  override val form = new RepresentativeContactFormProvider()()

  "RepresentativeContactView" must {

    val view = injectInstanceOf[RepresentativeContactView](Some(UserAnswers(afaId)))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)

    val renderedView: HtmlFormat.Appendable = view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.RepresentativeContactController.onSubmit(NormalMode, afaId).url,
      Seq("name", "companyName", "role", "telephone", "email")
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
