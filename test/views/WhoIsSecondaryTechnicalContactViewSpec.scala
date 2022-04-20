/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.WhoIsSecondaryTechnicalContactFormProvider
import models.{NormalMode, TechnicalContact, UserAnswers}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.WhoIsSecondaryTechnicalContactView

class WhoIsSecondaryTechnicalContactViewSpec extends TextInputViewBehaviours[TechnicalContact] {

  val messageKeyPrefix: String = "whoIsSecondaryTechnicalContact"
  val applicantName: String = "applicantName"

  override val form = new WhoIsSecondaryTechnicalContactFormProvider()()

  "WhoIsSecondaryTechnicalContactView" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[WhoIsSecondaryTechnicalContactView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)

    val renderedView: HtmlFormat.Appendable = view.apply(form, NormalMode, afaId, applicantName)(fakeRequest, messages)


    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix, Seq(applicantName))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.WhoIsSecondaryTechnicalContactController.onSubmit(NormalMode, afaId).url,
      Seq("name", "companyName", "telephone", "email")
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
