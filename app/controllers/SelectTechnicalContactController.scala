/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import forms.SelectTechnicalContactFormProvider
import models.requests.DataRequest
import models.{AfaId, ContactOptions, InternationalAddress, Mode, TechnicalContact, UkAddress, UserAnswers}
import navigation.Navigator
import pages._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{AfaService, ContactsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CommonHelpers
import views.html.SelectTechnicalContactView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SelectTechnicalContactController @Inject()(
                                                  override val messagesApi: MessagesApi,
                                                  afaService: AfaService,
                                                  navigator: Navigator,
                                                  identify: IdentifierAction,
                                                  getLock: LockAfaActionProvider,
                                                  contactsService: ContactsService,
                                                  getData: AfaDraftDataRetrievalAction,
                                                  requireData: DataRequiredAction,
                                                  formProvider: SelectTechnicalContactFormProvider,
                                                  val controllerComponents: MessagesControllerComponents,
                                                  view: SelectTechnicalContactView
                                                )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[ContactOptions] = formProvider()

  def onPageLoad(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>

      CommonHelpers.getApplicantName {
        companyName =>

          val preparedForm = request.userAnswers.get(SelectTechnicalContactPage) match {
            case None => form
            case Some(value) => form.fill(value)
          }

          val contacts: Seq[(ContactOptions, String)] = contactsService.contactsToRadioOptions(None)(request.userAnswers)
          Future.successful(Ok(view(preparedForm, mode, afaId, companyName, contacts)))
      }
  }

  def onSubmit(mode: Mode, afaId: AfaId): Action[AnyContent] = (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData).async {
    implicit request =>
      val contacts: Seq[(ContactOptions, String)] = contactsService.contactsToRadioOptions(None)(request.userAnswers)

      CommonHelpers.getApplicantName {
        companyName =>
          form.bindFromRequest().fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(view(formWithErrors, mode, afaId, companyName, contacts))),
            {
              case ContactOptions.SomeoneElse =>
                processManualContact(mode, request)
              case value if(ContactOptions.values.contains(value)) =>
                processContactCopy(mode, request, value)
              case _ => throw new IllegalArgumentException("SelectTechnicalContactController.onSubmit(). unrecognised contact option ")
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
      case isUkBased if (isUkBased.contains(true) && contactUKAddress.isDefined) =>
        updateUserAnswersUkAddress(mode, request, value, contact, contactIsUKBased, contactUKAddress)
      case isUkBased if (isUkBased.contains(false) && contactInternationalAddress.isDefined) =>
        updateUserAnswersInternationalAddress(mode, request, value, contact, contactIsUKBased, contactInternationalAddress)
      case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
    }
    //scalastyle:on simplify.boolean.expression
  }

  private def processManualContact(mode: Mode, request: DataRequest[AnyContent]) (implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectTechnicalContactPage, ContactOptions.SomeoneElse))
      _ <- afaService.set(updatedAnswers)
    } yield Redirect(navigator.nextPage(SelectTechnicalContactPage, mode, updatedAnswers))
  }

  private def updateUserAnswersUkAddress(
                                          mode: Mode,
                                          request: DataRequest[AnyContent],
                                          value: ContactOptions, contact: TechnicalContact,
                                          contactIsUKBased: Option[Boolean],
                                          contactUKAddress: Option[UkAddress]) (implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectTechnicalContactPage, value))
      updatedAnswersWithTechDetails <- Future.fromTry(updatedAnswers.set(WhoIsTechnicalContactPage, contact))
      updatedAnswersWithTechUkBased <- Future.fromTry(updatedAnswersWithTechDetails.set(IsTechnicalContactUkBasedPage, contactIsUKBased.get))
      updatedAnswersWithUKAddress <- Future.fromTry(updatedAnswersWithTechUkBased.set(TechnicalContactUkAddressPage, contactUKAddress.get))
      _ <- afaService.set(updatedAnswersWithUKAddress)
    } yield Redirect(navigator.nextPage(SelectTechnicalContactPage, mode, updatedAnswers))
  }

  private def updateUserAnswersInternationalAddress(
                                                     mode: Mode,
                                                     request: DataRequest[AnyContent],
                                                     value: ContactOptions, contact: TechnicalContact,
                                                     contactIsUKBased: Option[Boolean],
                                                     contactInternationalAddress: Option[InternationalAddress]) (implicit hc: HeaderCarrier): Future[Result] = {
    for {
      updatedAnswers <- Future.fromTry(request.userAnswers.set(SelectTechnicalContactPage, value))
      updatedAnswersWithTechDetails <- Future.fromTry(updatedAnswers.set(WhoIsTechnicalContactPage, contact))
      updatedAnswersWithTechUkBased <- Future.fromTry(updatedAnswersWithTechDetails.set(IsTechnicalContactUkBasedPage, contactIsUKBased.get))
      updatedAnswersWithTechAddress <- Future.fromTry(updatedAnswersWithTechUkBased.set(
        TechnicalContactInternationalAddressPage, contactInternationalAddress.get))
      _ <- afaService.set(updatedAnswersWithTechAddress)
    } yield Redirect(navigator.nextPage(SelectTechnicalContactPage, mode, updatedAnswers))
  }
}
