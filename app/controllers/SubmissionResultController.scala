/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import java.time.format.DateTimeFormatter
import controllers.actions._

import javax.inject.Inject
import models.AfaId
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AfaService, LockService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.SubmissionResult
import views.html.SubmissionResultView

import scala.concurrent.ExecutionContext

class SubmissionResultController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            identify: IdentifierAction,
                                            getLock: LockAfaActionProvider,
                                            getData: AfaDraftDataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            afaService: AfaService,
                                            lockService: LockService,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: SubmissionResultView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def onPageLoad(afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      for {
        afa <- afaService.submit(request.userAnswers)
        _   <- afaService.removeDraft(afa.id)
        _   <- lockService.removeLock(afa.id)
      } yield Ok(view(SubmissionResult(afa.id, afa.applicant.name, afa.expirationDate.format(dateFormatter))))
  }
}
