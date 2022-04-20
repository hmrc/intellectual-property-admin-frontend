/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import controllers.routes
import forms.WhoIsTechnicalContactFormProvider
import models.{NormalMode, TechnicalContact, UserAnswers}
import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behavioursDesignSystem.TextInputViewBehaviours
import views.html.WhoIsTechnicalContactView

class WhoIsTechnicalContactViewSpec extends TextInputViewBehaviours[TechnicalContact] {

  val messageKeyPrefix = "whoIsTechnicalContact"

  override val form = new WhoIsTechnicalContactFormProvider()()

  "WhoIsTechnicalContactView" must {

    val application = applicationBuilder(userAnswers = Some(UserAnswers(afaId))).build()

    val view = application.injector.instanceOf[WhoIsTechnicalContactView]

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    val renderedView: HtmlFormat.Appendable = view.apply(form, NormalMode, afaId)(fakeRequest, messages)

    behave like normalPageUsingDesignSystem(frontendAppConfig, applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithTextInputs(
      form,
      applyView,
      messageKeyPrefix,
      routes.WhoIsTechnicalContactController.onSubmit(NormalMode, afaId).url,
      Seq("contactName", "companyName", "contactTelephone", "otherTelephone", "contactEmail")
    )

    behave like pageWithGuidance(applyView(form), messageKeyPrefix, Seq("guidance"))

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(form))

    implicit val doc: Document = asDocument(renderedView)

    "display the correct contact name field hint" in {
      element("#contactName-hint").text() mustBe "For example, Jane Smith"
    }

    "display the correct telephone number field hint" in {
      element("#contactTelephone-hint").text() mustBe "For international numbers include the country code"
    }
  }
}


