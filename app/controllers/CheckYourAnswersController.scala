/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import com.google.inject.Inject
import controllers.actions.{AfaDraftDataRetrievalAction, DataRequiredAction, IdentifierAction, LockAfaActionProvider}
import models.AfaId
import models.requests.DataRequest
import navigation.Navigator
import pages.CompanyApplyingPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.EvidenceProvidedQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getLock: LockAfaActionProvider,
                                            getData: AfaDraftDataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            afaService: AfaService,
                                            navigator: Navigator
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      getCompanyName {
        companyName =>

          val noEvidenceProvided = request.userAnswers.get(EvidenceProvidedQuery).contains(false)
          val cyaHelper = new CheckYourAnswersHelper(request.userAnswers)


          val (call, key, canCreateAfa) =
            if (afaService.canCreateAfa(request.userAnswers)) {
              (routes.SubmissionResultController.onPageLoad(afaId), "checkYourAnswers.publish", true)
            } else {
              if (noEvidenceProvided) {
                (navigator.noEvidence(request.userAnswers), "checkYourAnswers.noEvidence", false)
              }
              else {
                (navigator.continue(request.userAnswers), "checkYourAnswers.continue", false)
              }
            }

          val sections = Seq(
            cyaHelper.applicationSection,
            cyaHelper.representativeSection,
            cyaHelper.legalContactSection,
            cyaHelper.secondaryLegalContactSection,
            cyaHelper.technicalContactSection,
            cyaHelper.secondaryTechnicalContactSection,
            cyaHelper.ipRightsSection(canCreateAfa),
            cyaHelper.additionalInformationSection
          ).flatten


          Future.successful(Ok(view(afaId, sections, call.url, key, canCreateAfa, noEvidenceProvided, companyName)))
      }
  }

  private def getCompanyName(block: String => Future[Result])
                            (implicit request: DataRequest[AnyContent], messages: Messages): Future[Result] = {

    request.userAnswers.get(CompanyApplyingPage).map {
      company =>

        block(company.acronym.getOrElse(company.name))
    }.getOrElse(block(messages("companyApplying.unknown")))
  }
}
