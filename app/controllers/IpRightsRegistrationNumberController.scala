/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.IpRightsRegistrationNumberFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, Mode}
import navigation.Navigator
import pages.{IpRightsRegistrationNumberPage, IpRightsTypePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.IprDetailsQuery
import services.AfaService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IpRightsRegistrationNumberView

import scala.concurrent.{ExecutionContext, Future}

class IpRightsRegistrationNumberController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      afaService: AfaService,
                                                      navigator: Navigator,
                                                      identify: IdentifierAction,
                                                      getLock: LockAfaActionProvider,
                                                      getData: AfaDraftDataRetrievalAction,
                                                      requireData: DataRequiredAction,
                                                      validateIndex: IpRightsIndexActionFilterProvider,
                                                      formProvider: IpRightsRegistrationNumberFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: IpRightsRegistrationNumberView
                                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {

      implicit request =>
        getIpRightsType(index) {
          ipRightsType =>
            getOtherExistingRegistrationNumbers(index) {
              regNums =>

                val form = formProvider(ipRightsType, regNums)

                val preparedForm = request.userAnswers.get(IpRightsRegistrationNumberPage(index)) match {
                  case None => form
                  case Some(value) => form.fill(value)
                }

                Future.successful(Ok(view(preparedForm, mode, index, afaId, ipRightsType)))
            }
        }
    }

  def onSubmit(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)).async {

      implicit request =>
        getIpRightsType(index) {
          ipRightsType =>
            getOtherExistingRegistrationNumbers(index) {
              regNums =>

              val form = formProvider(ipRightsType, regNums)

              form.bindFromRequest().fold(
                (formWithErrors: Form[_]) =>
                  Future.successful(BadRequest(view(formWithErrors, mode, index, afaId, ipRightsType))),

                value => {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(IpRightsRegistrationNumberPage(index), value))
                    _ <- afaService.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(IpRightsRegistrationNumberPage(index), mode, updatedAnswers))
                }
              )
            }
        }
  }

  private def getIpRightsType(index: Int)(block: String => Future[Result])
                              (implicit request: DataRequest[AnyContent], messages: Messages): Future[Result] = {

    request.userAnswers.get(IpRightsTypePage(index)).map {
      ipRightsType =>

        block(messages(s"ipRightsRegistrationNumber.$ipRightsType.name"))
    }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
  }

  private def getOtherExistingRegistrationNumbers(iprIndex: Int)
                                                 (block: Seq[String] => Future[Result])(implicit request: DataRequest[AnyContent]) = {

    request.userAnswers.get(IprDetailsQuery).map {
      existingIpRights =>

        val otherIpRightsWithRegNums = existingIpRights.flatMap(
          ipRight =>
          if (ipRight.registrationNumber.isDefined) Some(ipRight) else None
        )

        val otherRegNums = otherIpRightsWithRegNums.flatMap(_.registrationNumber).map(_.toUpperCase)

        block(
          if(request.userAnswers.get(IpRightsRegistrationNumberPage(iprIndex)).isDefined) {
            request.userAnswers.get(IpRightsRegistrationNumberPage(iprIndex)).map(
              reg => {

                otherRegNums.filter(_ != reg.toUpperCase)
              }
            ).toSeq.flatten
          } else {
            otherRegNums
          }
        )
    }.getOrElse(block(Seq.empty))
  }
}
