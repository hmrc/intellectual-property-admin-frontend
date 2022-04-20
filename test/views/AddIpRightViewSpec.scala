/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package views

import models.{NormalMode, UserAnswers}
import org.jsoup.nodes.Document
import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import viewmodels.{DisplayAnswerRow, IprReviewRow}
import views.behaviours.ViewBehaviours
import views.html.AddIpRightView

class AddIpRightViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "addIpRight"
  def actionSelector(index: Int): String = s"dl > div > dd:nth-child($index) > a"

  "AddIpRight view" must {

    val view = injectInstanceOf[AddIpRightView](Some(UserAnswers(afaId)))

    def applyView(numberOfIpRights: Int): HtmlFormat.Appendable = {
      val nextPage = Call("get", "anything")
      view.apply(NormalMode, afaId, Left(DisplayAnswerRow("", HtmlFormat.escape(""))), "", numberOfIpRights, nextPage)(fakeRequest, messages)
    }

    def applyViewLinks(): HtmlFormat.Appendable = {
      val nextPage = Call("get", "anything")
      view.apply(NormalMode, afaId, Right(Seq(IprReviewRow("Patent","description" ,"/delete", "/change", 0  ))), "/add", 1, nextPage)(fakeRequest, messages)
    }

    behave like normalPageUsingDesignSystem(
      frontendAppConfig,
      applyView(numberOfIpRights = 1),
      messageKeyPrefix, args = Seq(1),
      argsUsedInBrowserTitle = true
    )

    behave like pageWithBackLink(applyView(numberOfIpRights = 1))

    "display the correct plural message in the title when there are multiple IP Rights" in {
      val appliedView = applyView(numberOfIpRights = 2)

      implicit val doc: Document = asDocument(appliedView)

      val result = doc.getElementsByTag("title").text

      result mustEqual messages("addIpRight.heading.plural", 2) + s" - ${messages("site.service_name")}"

    }

    "display the correct change and delete links for the IprReviewRow list" in {
      val appliedView = applyViewLinks()

      implicit val doc: Document = asDocument(appliedView)

      val deleteLink = element(actionSelector(3))
      deleteLink.attr("href") mustEqual "/delete"
      elementText(actionSelector(3)) contains "Delete"

      val changeLink = element(actionSelector(4))
      changeLink.attr("href") mustEqual "/change"
      elementText(actionSelector(4)) contains "Change"
    }

    "display the correct right type and description in IprReviewRow list" in {
      val appliedView = applyViewLinks()

      implicit val doc: Document = asDocument(appliedView)

      elementText("dl > div > dt") contains "Patent"
      
      elementText("dl > div > dd:nth-child(2)") contains "description"
    }

    "display the correct add another right link" in {
      val appliedView = applyViewLinks()

      implicit val doc: Document = asDocument(appliedView)

      val addRightLink = element("#add-another-right")
      addRightLink.attr("href") mustEqual "/add"
      elementText("#add-another-right") contains "Add another right"
    }

    behave like pageWithSubmitButtonAndGoHomeLinkUsingDesignSystem(applyView(1))
  }
}
