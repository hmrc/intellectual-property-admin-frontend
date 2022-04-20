/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import controllers.actions._
import forms.SelectOtherTechnicalContactFormProvider

import javax.inject.Inject
import models.requests.DataRequest
import models.{AfaId, ContactOptions, InternationalAddress, Mode, TechnicalContact, UkAddress, UserAnswers}
import navigation.Navigator
import pages.{IsSecondaryTechnicalContactUkBasedPage,SecondaryTechnicalContactInternationalAddressPage,
  SecondaryTechnicalContactUkAddressPage, SelectOtherTechnicalContactPage, SelectTechnicalContactPage,
  WhoIsSecondaryTechnicalContactPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{AfaService, ContactsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CommonHelpers
import views.html.SelectOtherTechnicalContactView

import scala.concurrent.{ExecutionContext, Future}

class SelectOtherTechnicalContactController @Inject()(
                                                       override val messagesApi: MessagesApi,
                                                       afaService: AfaService,
                                                       navigator: Navigator,
                                                       identify: IdentifierAction,
                                                       getLock: LockAfaActionProvider,
                                                       contactsService: ContactsService,
                                                       getData: AfaDraftDataRetrievalAction,
                                                       requireData: DataRequiredAction,
                                                       formProvider: SelectOtherTechnicalContactFormProvider,
                                                       val controllerComponents: MessagesControllerComponents,
                                                       view: SelectOtherTechnicalContactView
                                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {


  private def form: Form[ContactOptions] = formProvider()


  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      CommonHelpers.getApplicantName {
        companyName =>

          val preparedForm = request.userAnswers.get(SelectOtherTechnicalContactPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val contacts = contactsService
            .contactsToRadioOptions(request.userAnswers.get(SelectTechnicalContactPage))(request.userAnswers)

          Future.successful(Ok(view(preparedForm, mode, afaId, companyName, contacts)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      implicit val answers: UserAnswers = request.userAnswers
      val contacts: Seq[(ContactOptions, String)] = contactsService.contactsToRadioOptions(answers.get(SelectTechnicalContactPage))
      CommonHelpers.getApplicantName {
        companyName =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, companyName, contacts))),
            {
              case ContactOptions.SomeoneElse =>
                processManualContact(mode, request)
              case value if (ContactOptions.values.contains(value)) =>
                processContactCopy(mode, request, value)
              case _ => throw new IllegalArgumentException("SelectOtherTechnicalContactController.onSubmit(). unrecognised contact option ")
            }
          )
      }
  }

  private def processContactCopy(mode: Mode, request: DataRequest[AnyContent], value: ContactOptions) (implicit hc: HeaderCarrier): Future[Result] = {
    implicit val answers: UserAnswers = request.userAnswers
    val contact: TechnicalContact = contactsService.getContact(value)
    val contactIsUKBased: Option[Boolean] = contactsService.getContactIsUKBased(value)
    val contactUKAddress: Option[UkAddress] = contactsService.getContactUKAddress(value)
    val contactInternationalAddress: Option[InternationalAddress] = contactsService.getContactInternationalAddress(value)

    //scalastyle:off simplify.boolean.expression
    contactIsUKBased match {
      case isUKBased if (isUKBased.contains(true) && contactUKAddress.isDefined) =>
        updateUserAnswersUkAddress(mode, request, value, contact, contactIsUKBased, contactUKAddress)
      case isUKBased if (isUKBased.contains(false) && contactInternationalAddress.isDefined) =>
        updateUserAnswersInternationalAddress(mode, request, value, contact, contactIsUKBased, contactInternationalAddress)
      case _ =>
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
    //scalastyle:on simplify.boolean.expression
  }

  private def processManualContact(mode: Mode, request: DataRequest[AnyContent]) (implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectOtherTechnicalContactPage, ContactOptions.SomeoneElse))
      _ <- afaService.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(SelectOtherTechnicalContactPage, mode, updatedAnswers))
  }

  private def updateUserAnswersUkAddress(
                                          mode: Mode,
                                          request: DataRequest[AnyContent],
                                          value: ContactOptions,
                                          contact: TechnicalContact,
                                          contactIsUKBased: Option[Boolean],
                                          contactUKAddress: Option[UkAddress]) (implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectOtherTechnicalContactPage, value))
      updatedAnswersWithTechDetails <- Future.fromTry(updatedAnswers.set(WhoIsSecondaryTechnicalContactPage, contact))
      updatedAnswersWithTechUkBased <- Future.fromTry(updatedAnswersWithTechDetails.set(IsSecondaryTechnicalContactUkBasedPage, contactIsUKBased.get))
      updatedAnswersWithUKAddress <- Future.fromTry(updatedAnswersWithTechUkBased.set(SecondaryTechnicalContactUkAddressPage, contactUKAddress.get))
      _ <- afaService.set(updatedAnswersWithUKAddress)
    } yield Redirect(navigator.nextPage(SelectOtherTechnicalContactPage, mode, updatedAnswers))
  }

  private def updateUserAnswersInternationalAddress(
                                                     mode: Mode,
                                                     request: DataRequest[AnyContent],
                                                     value: ContactOptions,
                                                     contact: TechnicalContact,
                                                     contactIsUKBased: Option[Boolean],
                                                     contactInternationalAddress: Option[InternationalAddress])(implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectOtherTechnicalContactPage, value))
      updatedAnswersWithTechDetails <- Future.fromTry(updatedAnswers.set(WhoIsSecondaryTechnicalContactPage, contact))
      updatedAnswersWithTechUkBased <- Future.fromTry(updatedAnswersWithTechDetails.set(IsSecondaryTechnicalContactUkBasedPage, contactIsUKBased.get))
      updatedAnswersWithInternationalAddress <- Future.fromTry(updatedAnswersWithTechUkBased.set(
        SecondaryTechnicalContactInternationalAddressPage, contactInternationalAddress.get))
      _ <- afaService.set(updatedAnswersWithInternationalAddress)
    } yield Redirect(navigator.nextPage(SelectOtherTechnicalContactPage, mode, updatedAnswers))
  }
}
