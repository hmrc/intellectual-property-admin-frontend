/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import java.time.LocalDate
import controllers.actions._
import forms.ApplicationReceiptDateFormProvider

import javax.inject.Inject
import models.auditing.DraftStarted
import models.{AfaId, Mode, Region, UserAnswers}
import navigation.Navigator
import pages.ApplicationReceiptDatePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.PublicationDeadlineQuery
import services.AfaService
import services.{AfaService, WorkingDaysService}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ApplicationReceiptDateView

import scala.concurrent.{ExecutionContext, Future}

class ApplicationReceiptDateController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  afaService: AfaService,
                                                  navigator: Navigator,
                                                  identify: IdentifierAction,
                                                  getLock: LockAfaActionProvider,
                                                  getData: AfaDraftDataRetrievalAction,
                                                  formProvider: ApplicationReceiptDateFormProvider,
                                                  auditConnector: AuditConnector,
                                                  workingDaysService: WorkingDaysService,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: ApplicationReceiptDateView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[LocalDate] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId)) {
    implicit request =>

      val value = request.userAnswers.flatMap(_.get(ApplicationReceiptDatePage))

      val preparedForm = value match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, afaId))
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId)).async {
    implicit request =>

      val userAnswers = request.userAnswers.getOrElse(UserAnswers(afaId))

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, afaId))),

        value => {

          val daysAllowed = 30

          workingDaysService.workingDays(Region.EnglandAndWales, value, daysAllowed).flatMap {
            publicationDeadline =>

              val answers = userAnswers.set(ApplicationReceiptDatePage, value).flatMap {
                _.set(PublicationDeadlineQuery, publicationDeadline)
              }

              for {
                updatedAnswers <- Future.fromTry(answers)
                _              <- afaService.set(updatedAnswers)
              } yield {

                auditConnector.sendExplicitAudit("startedApplicationForAction", DraftStarted(
                  id       = afaId,
                  userName = request.name,
                  PID   = request.internalId
                ))

                Redirect(navigator.nextPage(ApplicationReceiptDatePage, mode, updatedAnswers))
              }
          }
        }
      )
  }
}
