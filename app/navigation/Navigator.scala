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

package navigation

import controllers.routes
import javax.inject.{Inject, Singleton}
import models.CompanyApplyingIsRightsHolder._
import models._
import pages._
import play.api.libs.json.Reads
import play.api.mvc.Call
import queries.{IprDetailsQuery, NiceClassIdsQuery}

//scalastyle:off
@Singleton
class Navigator @Inject()() {
  private val normalRoutes: Page => UserAnswers => Call = {
    //Application section
    case ApplicationReceiptDatePage                             => ua => routes.CompanyApplyingController.onPageLoad(NormalMode, ua.id)
    case AdditionalInfoProvidedPage                             => additionalInfoProvidedRoute(NormalMode)
    case RestrictedHandlingPage                                 => ua => routes.PermissionToDestroySmallConsignmentsController.onPageLoad(NormalMode, ua.id)
    case PermissionToDestroySmallConsignmentsPage               => ua => routes.IsExOfficioController.onPageLoad(NormalMode, ua.id)
    case IsExOfficioPage                                        => isExOfficoRoute
    case WantsOneYearRightsProtectionPage                       => ua => routes.CheckYourAnswersController.onPageLoad(ua.id)
    case CompanyApplyingPage                                    => ua => routes.IsCompanyApplyingUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsCompanyApplyingUkBasedPage                           => isCompanyApplyingUkBasedRoute
    case CompanyApplyingUkAddressPage                           => ua => routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, ua.id)
    case CompanyApplyingInternationalAddressPage                => ua => routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, ua.id)
    case CompanyApplyingIsRightsHolderPage                      => isApplicantRightsHolder
    //Representative Section
    case RepresentativeDetailsPage                              => ua => routes.EvidenceOfPowerToActController.onPageLoad(NormalMode, ua.id)
    case EvidenceOfPowerToActPage                               => ua => routes.IsRepresentativeContactUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsRepresentativeContactUkBasedPage                     => isRepresentativeContactUkBasedRoute
    case RepresentativeContactUkAddressPage                     => ua => routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, ua.id)
    case RepresentativeContactInternationalAddressPage          => ua => routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, ua.id)
    //Legal contact section
    case IsRepresentativeContactLegalContactPage                => isRepresentativeContactLegalContactRoute
    case ApplicantLegalContactPage                              => ua => routes.IsApplicantLegalContactUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsApplicantLegalContactUkBasedPage                     => isApplicantLegalContactUkBasedRoute
    case ApplicantLegalContactUkAddressPage                     => ua => routes.AddAnotherLegalContactController.onPageLoad(NormalMode, ua.id)
    case ApplicantLegalContactInternationalAddressPage          => ua => routes.AddAnotherLegalContactController.onPageLoad(NormalMode, ua.id)
    //Secondary Legal contact section
    case AddAnotherLegalContactPage                             => ua => addAnotherLegalContactRoute(ua)
    case WhoIsSecondaryLegalContactPage                         => ua => routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsApplicantSecondaryLegalContactUkBasedPage            => isApplicantSecondaryLegalContactUkBasedRoute
    case ApplicantSecondaryLegalContactUkAddressPage            => ua => routes.SelectTechnicalContactController.onPageLoad(NormalMode, ua.id)
    case ApplicantSecondaryLegalContactInternationalAddressPage => ua => routes.SelectTechnicalContactController.onPageLoad(NormalMode, ua.id)
    // Technical contact section
    case SelectTechnicalContactPage                             => selectTechnicalContactRoute
    case WhoIsTechnicalContactPage                              => ua => routes.IsTechnicalContactUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsTechnicalContactUkBasedPage                          => isTechnicalContactUkBasedRoute
    case TechnicalContactUkAddressPage                          => ua => routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, ua.id)
    case TechnicalContactInternationalAddressPage               => ua => routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, ua.id)
    //Secondary Technical contact section
    case AddAnotherTechnicalContactPage                         =>  ua => addAnotherTechnicalContactRoute(ua)
    case SelectOtherTechnicalContactPage                        => ua => selectOtherTechnicalContactRoute(ua)
    case WhoIsSecondaryTechnicalContactPage                     => ua => routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(NormalMode, ua.id)
    case IsSecondaryTechnicalContactUkBasedPage                 => isSecondaryTechnicalContactUkBasedRoute
    case SecondaryTechnicalContactInternationalAddressPage      => ua => routes.IpRightsTypeController.onPageLoad(NormalMode, 0, ua.id)
    case SecondaryTechnicalContactUkAddressPage                 => ua => routes.IpRightsTypeController.onPageLoad(NormalMode, 0, ua.id)


    // ------------------------- Break out of flow if rights exist here -------------------------
    // Adding rights
    case IpRightsTypePage(id)                                   => ipRightsTypeRoute(id)
    case IpRightsRegistrationNumberPage(id)                     => ua => routes.IpRightsRegistrationEndController.onPageLoad(NormalMode, id, ua.id)
    case IpRightsSupplementaryProtectionCertificateTypePage(id) => ua => routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, id, ua.id)
    case IpRightsRegistrationEndPage(id)                        => ipRightsRegistrationEndRoute(id)
    case IpRightsNiceClassPage(iprIndex, _)                     => ua => routes.IpRightsAddNiceClassController.onPageLoad(NormalMode, iprIndex, ua.id)
    case IpRightsAddNiceClassPage(iprIndex)                     => addNiceClassRoute(NormalMode, iprIndex)
    case IpRightsDescriptionPage(iprIndex)                      => ua => routes.CheckIprDetailsController.onPageLoad(NormalMode, iprIndex, ua.id)
    case IpRightsDescriptionWithBrandPage(id)                   => ua => routes.IpRightsNiceClassController.onPageLoad(NormalMode, id, 0, ua.id)
    case AddIpRightPage                                         => addIpRightRoute(NormalMode)
    case IpRightsRemoveNiceClassPage(iprIndex)                  => removeNiceClassRoute(NormalMode, iprIndex)
    case RemoveIprPage                                          => removeIprRoute
    case DeleteIpRightPage(iprIndex)                            => deleteIpRightRoute(iprIndex)
    case DeleteNiceClassPage(iprIndex, niceClassIndex)          => deleteNiceClassRoute(NormalMode, iprIndex, niceClassIndex)
    case CheckIprDetailsPage(_)                                 => ua => routes.AddIpRightController.onPageLoad(NormalMode, ua.id)
    case UnlockAfaPage                                          => unlockAfaRoute
    case DeleteDraftPage                                        => _ => routes.ViewDraftsController.onPageLoad()
    case _                                                      => _ => controllers.routes.CreateAfaIdController.onPageLoad
  }

  private def additionalInfoProvidedRoute(mode: Mode)(answers:UserAnswers): Call = {
    (mode, answers.get(AdditionalInfoProvidedPage), answers.get(RestrictedHandlingPage)) match {
      case (NormalMode, Some(true), _)      => routes.RestrictedHandlingController.onPageLoad(mode, answers.id)
      case (NormalMode, Some(false), _)     => routes.PermissionToDestroySmallConsignmentsController.onPageLoad(mode, answers.id)
      case (CheckMode, Some(true), None)    => routes.RestrictedHandlingController.onPageLoad(mode, answers.id)
      case _                                => routes.CheckYourAnswersController.onPageLoad(answers.id)
    }
  }

  private def deleteIpRightRoute(index: Int)(answers: UserAnswers): Call = {
    answers.get(DeleteIpRightPage(index)) match {
      case Some(true) => routes.AddIpRightController.onDelete(NormalMode, answers.id, index)
      case _ => routes.AddIpRightController.onPageLoad(NormalMode, answers.id)
    }
  }

  private def deleteNiceClassRoute(mode: Mode, iprIndex: Int, niceClassIndex: Int)(answers: UserAnswers): Call = {
    val Some(niceClasses) = answers.get(NiceClassIdsQuery(iprIndex))
    answers.get(DeleteNiceClassPage(iprIndex, niceClassIndex)) match {
      case Some(true) if niceClasses.nonEmpty && niceClasses.size > 1 =>
        routes.IpRightsAddNiceClassController.onDelete(mode, iprIndex, niceClassIndex, answers.id)
      case _ => routes.IpRightsAddNiceClassController.onPageLoad(mode, iprIndex, answers.id)
    }
  }

  private def isApplicantRightsHolder(answers: UserAnswers): Call = answers.get(CompanyApplyingIsRightsHolderPage) match {
    case Some(_) => routes.RepresentativeContactController.onPageLoad(NormalMode, answers.id)
    case None               => routes.SessionExpiredController.onPageLoad
  }

  private def selectTechnicalContactRoute(userAnswers: UserAnswers): Call = userAnswers.get(SelectTechnicalContactPage) match {
    case Some(ContactOptions.SomeoneElse) => routes.WhoIsTechnicalContactController.onPageLoad(NormalMode, userAnswers.id)
    case Some(_)              => routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, userAnswers.id)
    case _                    => routes.SessionExpiredController.onPageLoad
  }

  private def selectOtherTechnicalContactRoute(userAnswers: UserAnswers): Call = userAnswers.get(SelectOtherTechnicalContactPage) match{
    case Some(ContactOptions.SomeoneElse) => routes.WhoIsSecondaryTechnicalContactController.onPageLoad(NormalMode, userAnswers.id)
    case Some(_)              => routes.IpRightsTypeController.onPageLoad(NormalMode, 0, userAnswers.id)
    case _                    => routes.SessionExpiredController.onPageLoad
  }

  private def unlockAfaRoute(answers: UserAnswers): Call = answers.get(UnlockAfaPage) match {
    case Some(true) =>
      routes.CheckYourAnswersController.onPageLoad(answers.id)
    case _ =>
      routes.ViewDraftsController.onPageLoad()
  }

  private def removeIprRoute(answers: UserAnswers): Call = answers.get(IprDetailsQuery) match {
    case Some(iprs) if iprs.nonEmpty =>
      routes.AddIpRightController.onPageLoad(NormalMode, answers.id)
    case _ =>
      routes.IpRightsTypeController.onPageLoad(NormalMode, 0, answers.id)
  }

  private def removeNiceClassRoute(mode: Mode, iprIndex: Int)(answers: UserAnswers): Call = {

    val initialNiceClassIndex = 0
    
    answers.get(NiceClassIdsQuery(iprIndex)) match {
      case Some(niceClasses) if niceClasses.nonEmpty =>
        routes.IpRightsAddNiceClassController.onPageLoad(mode, iprIndex, answers.id)
      case _ =>
        routes.IpRightsNiceClassController.onPageLoad(mode, iprIndex, initialNiceClassIndex, answers.id)
    }
  }

  private def addNiceClassRoute(mode: Mode, iprIndex: Int)(answers: UserAnswers): Call =
    answers.get(NiceClassIdsQuery(iprIndex)) match {
      case Some(classes) => routes.IpRightsNiceClassController.onPageLoad(mode, iprIndex, classes.size, answers.id)
      case None          => routes.IpRightsNiceClassController.onPageLoad(mode, iprIndex, 0, answers.id)
    }

  private def addIpRightRoute(mode: Mode)(answers: UserAnswers): Call =
    answers.get(IprDetailsQuery) match {
      case Some(details)  => routes.IpRightsTypeController.onPageLoad(mode, details.size, answers.id)
      case _              => routes.IpRightsTypeController.onPageLoad(mode, 0, answers.id)
    }

  private def isExOfficoRoute(answers: UserAnswers): Call = answers.get(IsExOfficioPage) match {
    case Some(true)  => routes.WantsOneYearRightsProtectionController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.CheckYourAnswersController.onPageLoad(answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def isRepresentativeContactLegalContactRoute(userAnswers: UserAnswers): Call = userAnswers.get(IsRepresentativeContactLegalContactPage) match {
    case Some(true) => routes.AddAnotherLegalContactController.onPageLoad(NormalMode, userAnswers.id)
    case Some(false) => routes.ApplicantLegalContactController.onPageLoad(NormalMode, userAnswers.id)
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def isRepresentativeContactUkBasedRoute(answers: UserAnswers): Call = answers.get(IsRepresentativeContactUkBasedPage) match {
    case Some(true)  => routes.RepresentativeContactUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.RepresentativeContactInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def isCompanyApplyingUkBasedRoute(answers: UserAnswers): Call = answers.get(IsCompanyApplyingUkBasedPage) match {
    case Some(true)  => routes.CompanyApplyingUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.CompanyApplyingInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def isApplicantLegalContactUkBasedRoute(answers: UserAnswers): Call = answers.get(IsApplicantLegalContactUkBasedPage) match {
    case Some(true)  => routes.ApplicantLegalContactUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.ApplicantLegalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def addAnotherLegalContactRoute(userAnswers: UserAnswers): Call = userAnswers.get(AddAnotherLegalContactPage) match {
    case Some(true)   => routes.WhoIsSecondaryLegalContactController.onPageLoad(NormalMode, userAnswers.id)
    case Some(false)  => routes.SelectTechnicalContactController.onPageLoad(NormalMode, userAnswers.id)
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def isApplicantSecondaryLegalContactUkBasedRoute(answers: UserAnswers): Call = answers.get(IsApplicantSecondaryLegalContactUkBasedPage) match {
    case Some(true)  => routes.ApplicantSecondaryLegalContactUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.ApplicantSecondaryLegalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }
  private def isTechnicalContactUkBasedRoute(answers: UserAnswers): Call = answers.get(IsTechnicalContactUkBasedPage) match {
    case Some(true)  => routes.TechnicalContactUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.TechnicalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def addAnotherTechnicalContactRoute(userAnswers: UserAnswers): Call = userAnswers.get(AddAnotherTechnicalContactPage) match {
    case Some(true) => routes.SelectOtherTechnicalContactController.onPageLoad(NormalMode, userAnswers.id)
    case Some(false) => routes.IpRightsTypeController.onPageLoad(NormalMode, 0, userAnswers.id)
    case _ => routes.SessionExpiredController.onPageLoad
  }

  private def isSecondaryTechnicalContactUkBasedRoute(answers: UserAnswers): Call = answers.get(IsSecondaryTechnicalContactUkBasedPage) match {
    case Some(true)  => routes.SecondaryTechnicalContactUkAddressController.onPageLoad(NormalMode, answers.id)
    case Some(false) => routes.SecondaryTechnicalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)
    case None        => routes.SessionExpiredController.onPageLoad
  }

  private def ipRightsTypeRoute(iprIndex: Int)(answers: UserAnswers): Call = {

    import IpRightsType._

    answers.get(IpRightsTypePage(iprIndex)) match {
      case Some(SupplementaryProtectionCertificate) =>
        routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(NormalMode, iprIndex, answers.id)
      case Some(Trademark) | Some(Design) | Some(Patent) =>
        routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, iprIndex, answers.id)
      case _ =>
        routes.IpRightsDescriptionController.onPageLoad(NormalMode, iprIndex, answers.id)
    }
  }

  private def ipRightsRegistrationEndRoute(iprIndex: Int)(answers: UserAnswers): Call =
    answers.get(IpRightsTypePage(iprIndex)) match {
      case Some(IpRightsType.Trademark) => routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, iprIndex, answers.id)
      case _                            => routes.IpRightsDescriptionController.onPageLoad(NormalMode, iprIndex, answers.id)
    }

  private val checkRouteMap: (Page, Mode) => UserAnswers => Call = {
    case (AdditionalInfoProvidedPage, _mode)                               => additionalInfoProvidedRoute(_mode)
    case (IsExOfficioPage, _)                                              => isExOfficioCheckRoute
    case (IsCompanyApplyingUkBasedPage, _)                                 => isCompanyApplyingUkBasedCheckRoute
    case (IsRepresentativeContactUkBasedPage,_)                            => isRepresentativeContactUkBasedCheckRoute
    case (IsRepresentativeContactLegalContactPage,_)                       => isRepresentativeContactLegalContactCheckRoute
    case (ApplicantLegalContactPage,_)                                     => applicantLegalContactCheckRoute
    case (IsApplicantLegalContactUkBasedPage,_)                            => isApplicantLegalContactUkBasedCheckRoute
    case (IsApplicantSecondaryLegalContactUkBasedPage,_)                   => isApplicantSecondaryLegalContactUkBasedCheckRoute
    case (WhoIsTechnicalContactPage,_)                                     => whoIsTechnicalContactCheckRoute
    case (IsTechnicalContactUkBasedPage,_)                                 => isTechnicalContactUkBasedCheckRoute
    case (IsSecondaryTechnicalContactUkBasedPage,_)                        => isSecondaryTechnicalContactUkBasedCheckRoute
    case (IpRightsTypePage(index),_mode)                                   => ipRightsTypeCheckRoute(_mode, index)
    case (IpRightsRegistrationNumberPage(index),_mode)                     => ipRightsRegistrationNumberCheckRoute(_mode, index)
    case (IpRightsRegistrationEndPage(index),_mode)                        => ipRightsRegistrationEndCheckRoute(_mode, index)
    case (IpRightsDescriptionPage(index),_mode)                            => ipRightsDescriptionCheckRoute(_mode,  index)
    case (IpRightsDescriptionWithBrandPage(index),_mode)                   => ipRightsDescriptionCheckRoute(_mode, index)
    case (IpRightsSupplementaryProtectionCertificateTypePage(index),_mode) => ipRightsSupplementaryProtectionCertificateTypeCheckRoute(_mode, index)
    case (IpRightsNiceClassPage(iprIndex, _),_mode)                        => ua => routes.IpRightsAddNiceClassController.onPageLoad(_mode, iprIndex, ua.id)
    case (DeleteNiceClassPage(iprIndex, niceClassIndex),_mode)             => deleteNiceClassRoute(_mode, iprIndex, niceClassIndex)
    case (IpRightsRemoveNiceClassPage(iprIndex),_mode)                     => removeNiceClassRoute(_mode, iprIndex)
    case (IpRightsAddNiceClassPage(iprIndex),_mode)                        => addNiceClassRoute(_mode, iprIndex)
    case (AddIpRightPage,_mode)                                            => addIpRightRoute(_mode)
    case (DeleteIpRightPage(iprIndex), _mode)                              => deleteIpRightCheckRoute(_mode, iprIndex)
    case (RemoveIprPage, _mode)                                            => removeIprCheckRoute(_mode)
    case (WhoIsSecondaryLegalContactPage, _mode)                           => secondaryLegalContactCheckRoute(_mode)
    case (SelectOtherTechnicalContactPage, _mode)                          => selectOtherTechnicalContactCheckRoute(_mode)
    case (WhoIsSecondaryTechnicalContactPage, _mode)                       => secondaryTechnicalContactCheckRoute(_mode)
    case _                                                                 => ua => routes.CheckYourAnswersController.onPageLoad(ua.id)
  }

  private def isExOfficioCheckRoute(answers: UserAnswers): Call =
    (answers.get(IsExOfficioPage), answers.get(WantsOneYearRightsProtectionPage)) match {
      case (Some(true), None) => routes.WantsOneYearRightsProtectionController.onPageLoad(CheckMode, answers.id)
      case _                  => routes.CheckYourAnswersController.onPageLoad(answers.id)
    }

  private def isCompanyApplyingUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsCompanyApplyingUkBasedPage), ua.get(CompanyApplyingUkAddressPage), ua.get(CompanyApplyingInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.CompanyApplyingUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.CompanyApplyingInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isRepresentativeContactUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsRepresentativeContactUkBasedPage), ua.get(RepresentativeContactUkAddressPage), ua.get(RepresentativeContactInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.RepresentativeContactUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.RepresentativeContactInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isRepresentativeContactLegalContactCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsRepresentativeContactLegalContactPage), ua.get(ApplicantLegalContactPage)) match {
      case (Some(false), None) => routes.ApplicantLegalContactController.onPageLoad(CheckMode, ua.id)
      case _                   => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isApplicantLegalContactUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsApplicantLegalContactUkBasedPage), ua.get(ApplicantLegalContactUkAddressPage), ua.get(ApplicantLegalContactInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.ApplicantLegalContactUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.ApplicantLegalContactInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def secondaryLegalContactCheckRoute(mode: Mode)(ua: UserAnswers): Call =
    ua.get(IsApplicantSecondaryLegalContactUkBasedPage) match {
      case None => routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(mode, ua.id)
      case _    => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isApplicantSecondaryLegalContactUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsApplicantSecondaryLegalContactUkBasedPage), ua.get(ApplicantSecondaryLegalContactUkAddressPage),
      ua.get(ApplicantSecondaryLegalContactInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.ApplicantSecondaryLegalContactUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.ApplicantSecondaryLegalContactInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def whoIsTechnicalContactCheckRoute(ua: UserAnswers): Call = ua.get(IsTechnicalContactUkBasedPage) match {
    case Some(_) => routes.CheckYourAnswersController.onPageLoad(ua.id)
    case None    => routes.IsTechnicalContactUkBasedController.onPageLoad(CheckMode, ua.id)
  }

  private def applicantLegalContactCheckRoute(ua: UserAnswers): Call = ua.get(IsApplicantLegalContactUkBasedPage) match {
    case Some(_) => routes.CheckYourAnswersController.onPageLoad(ua.id)
    case None    => routes.IsApplicantLegalContactUkBasedController.onPageLoad(CheckMode, ua.id)
  }

  private def isTechnicalContactUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsTechnicalContactUkBasedPage), ua.get(TechnicalContactUkAddressPage), ua.get(TechnicalContactInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.TechnicalContactUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.TechnicalContactInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def selectOtherTechnicalContactCheckRoute(mode: Mode)(ua: UserAnswers): Call =
    (ua.get(SelectOtherTechnicalContactPage)) match {
      case Some(ContactOptions.SomeoneElse) => routes.WhoIsSecondaryTechnicalContactController.onPageLoad(mode, ua.id)
      case _ => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def secondaryTechnicalContactCheckRoute(mode: Mode)(ua: UserAnswers): Call =
    ua.get(IsSecondaryTechnicalContactUkBasedPage) match {
      case None => routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(mode, ua.id)
      case _ => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def isSecondaryTechnicalContactUkBasedCheckRoute(ua: UserAnswers): Call =
    (ua.get(IsSecondaryTechnicalContactUkBasedPage), ua.get(SecondaryTechnicalContactUkAddressPage),
      ua.get(SecondaryTechnicalContactInternationalAddressPage)) match {
      case (Some(true), None, _)  => routes.SecondaryTechnicalContactUkAddressController.onPageLoad(CheckMode, ua.id)
      case (Some(false), _, None) => routes.SecondaryTechnicalContactInternationalAddressController.onPageLoad(CheckMode, ua.id)
      case _                      => routes.CheckYourAnswersController.onPageLoad(ua.id)
    }

  private def ipRightsSupplementaryProtectionCertificateTypeCheckRoute(mode: Mode, index: Int)(ua: UserAnswers): Call = {
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode

    ua.get(IpRightsRegistrationNumberPage(index)) match {
      case Some(_) => routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
      case None    => routes.IpRightsRegistrationNumberController.onPageLoad(mode, index, ua.id)
    }
  }

  private def ipRightsTypeCheckRoute(mode: Mode, index: Int)(ua: UserAnswers): Call = {

    import IpRightsType._
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode

    ua.get(IpRightsTypePage(index)) match {
      case Some(SupplementaryProtectionCertificate) =>
        if (ua.get(IpRightsSupplementaryProtectionCertificateTypePage(index)).isDefined) {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        } else {
          routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(mode, index, ua.id)
        }
      case Some(Trademark) =>
        if (ua.get(IpRightsRegistrationNumberPage(index)).isEmpty) {
          routes.IpRightsRegistrationNumberController.onPageLoad(mode, index, ua.id)
        } else if (ua.get(IpRightsDescriptionWithBrandPage(index)).isEmpty) {
          routes.IpRightsDescriptionWithBrandController.onPageLoad(mode, index, ua.id)
        } else {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        }
      case Some(Design) | Some(Patent) =>
        if (ua.get(IpRightsRegistrationNumberPage(index)).isEmpty) {
          routes.IpRightsRegistrationNumberController.onPageLoad(mode, index, ua.id)
        } else if (ua.get(IpRightsDescriptionPage(index)).isEmpty) {
          routes.IpRightsDescriptionController.onPageLoad(mode, index,ua.id)
        } else {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        }
      case _ =>
        if (ua.get(IpRightsDescriptionPage(index)).isDefined) {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        } else {
          routes.IpRightsDescriptionController.onPageLoad(mode, index, ua.id)
        }
    }
  }

  private def ipRightsDescriptionCheckRoute(mode: Mode, index: Int)(ua: UserAnswers): Call ={
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode

    ua.get(IpRightsTypePage(index)) match {
      case Some(IpRightsType.Trademark) =>
        if (ua.get(IpRightsNiceClassPage(index, 0)).isDefined) {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        } else {
          routes.IpRightsNiceClassController.onPageLoad(returnMode, index, 0, ua.id)
        }

      case _ =>
        routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
    }
  }

  private def ipRightsRegistrationNumberCheckRoute(mode:Mode, index: Int)(ua: UserAnswers): Call = {
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode
    if (ua.get(IpRightsRegistrationEndPage(index)).isDefined) {
      routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
    } else {
      routes.IpRightsRegistrationEndController.onPageLoad(mode, index, ua.id)
    }
  }

  private def ipRightsRegistrationEndCheckRoute(mode: Mode, index: Int)(ua: UserAnswers): Call = {

    import models.IpRightsType._
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode

    ua.get(IpRightsTypePage(index)) match {
      case Some(Trademark) =>
        if (ua.get(IpRightsDescriptionWithBrandPage(index)).isDefined) {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        } else {
          routes.IpRightsDescriptionWithBrandController.onPageLoad(mode, index, ua.id)
        }

      case _ =>
        if(ua.get(IpRightsDescriptionPage(index)).isDefined) {
          routes.CheckIprDetailsController.onPageLoad(returnMode, index, ua.id)
        } else {
          routes.IpRightsDescriptionController.onPageLoad(mode, index, ua.id)
        }
    }
  }

  private def deleteIpRightCheckRoute(mode:Mode, index: Int)(answers: UserAnswers): Call = {
    val returnMode = if (mode == ModifyMode) NormalMode else CheckMode

    answers.get(DeleteIpRightPage(index)) match {
      case Some(true) => routes.AddIpRightController.onDelete(returnMode, answers.id, index)
      case _ => routes.AddIpRightController.onPageLoad(returnMode, answers.id)
    }
  }

  private def removeIprCheckRoute(mode:Mode)(answers: UserAnswers): Call = answers.get(IprDetailsQuery) match {
    case Some(iprs) if iprs.nonEmpty =>
      routes.AddIpRightController.onPageLoad(mode, answers.id)
    case _ =>
      routes.IpRightsTypeController.onPageLoad(mode, 0, answers.id)
  }

  def firstPage(afaId: AfaId): Call =
    controllers.routes.ApplicationReceiptDateController.onPageLoad(NormalMode, afaId)

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case _ =>
      checkRouteMap(page, mode)(userAnswers)
  }

  protected def mainPageStatuses(answers: UserAnswers): Seq[PageStatus[_]] =
    Seq(
      PageStatus(ApplicationReceiptDatePage, answers, routes.ApplicationReceiptDateController.onPageLoad(NormalMode, answers.id)),
      PageStatus(CompanyApplyingPage, answers, routes.CompanyApplyingController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsCompanyApplyingUkBasedPage, answers, routes.IsCompanyApplyingUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(CompanyApplyingUkAddressPage, answers, routes.CompanyApplyingUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(CompanyApplyingInternationalAddressPage, answers, routes.CompanyApplyingInternationalAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(CompanyApplyingIsRightsHolderPage, answers, routes.CompanyApplyingIsRightsHolderController.onPageLoad(NormalMode, answers.id)),
      PageStatus(RepresentativeDetailsPage, answers, routes.RepresentativeContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(EvidenceOfPowerToActPage, answers, routes.EvidenceOfPowerToActController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsRepresentativeContactUkBasedPage, answers, routes.IsRepresentativeContactUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(RepresentativeContactUkAddressPage, answers, routes.RepresentativeContactUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(RepresentativeContactInternationalAddressPage, answers, routes.RepresentativeContactInternationalAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsRepresentativeContactLegalContactPage, answers, routes.IsRepresentativeContactLegalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(ApplicantLegalContactPage, answers, routes.ApplicantLegalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsApplicantLegalContactUkBasedPage, answers, routes.IsApplicantLegalContactUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(ApplicantLegalContactUkAddressPage, answers, routes.ApplicantLegalContactUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(ApplicantLegalContactInternationalAddressPage, answers, routes.ApplicantLegalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)),

      PageStatus(AddAnotherLegalContactPage, answers, routes.AddAnotherLegalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(WhoIsSecondaryLegalContactPage, answers, routes.WhoIsSecondaryLegalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsApplicantSecondaryLegalContactUkBasedPage, answers, routes.IsApplicantSecondaryLegalContactUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(ApplicantSecondaryLegalContactUkAddressPage, answers, routes.ApplicantSecondaryLegalContactUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(ApplicantSecondaryLegalContactInternationalAddressPage, answers, routes.ApplicantSecondaryLegalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(SelectTechnicalContactPage, answers, routes.SelectTechnicalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(WhoIsTechnicalContactPage, answers, routes.WhoIsTechnicalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsTechnicalContactUkBasedPage, answers, routes.IsTechnicalContactUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(TechnicalContactUkAddressPage, answers, routes.TechnicalContactUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(TechnicalContactInternationalAddressPage, answers, routes.TechnicalContactInternationalAddressController.onPageLoad(NormalMode, answers.id)) ,


      PageStatus(AddAnotherTechnicalContactPage, answers, routes.AddAnotherTechnicalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(SelectOtherTechnicalContactPage, answers, routes.SelectOtherTechnicalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(WhoIsSecondaryTechnicalContactPage, answers, routes.WhoIsSecondaryTechnicalContactController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsSecondaryTechnicalContactUkBasedPage, answers, routes.IsSecondaryTechnicalContactUkBasedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(SecondaryTechnicalContactUkAddressPage, answers, routes.SecondaryTechnicalContactUkAddressController.onPageLoad(NormalMode, answers.id)),
      PageStatus(SecondaryTechnicalContactInternationalAddressPage, answers, routes.SecondaryTechnicalContactInternationalAddressController.onPageLoad(NormalMode, answers.id))

    )

  protected def iprPageStatuses(answers: UserAnswers): Seq[PageStatus[_]] =
    answers.get(IprDetailsQuery) match {
      case Some(iprs) if iprs.nonEmpty =>
        iprs.zipWithIndex.flatMap {
          case (_, index) =>

            Seq(
              PageStatus(IpRightsTypePage(index), answers, routes.IpRightsTypeController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsSupplementaryProtectionCertificateTypePage(index), answers, routes.IpRightsSupplementaryProtectionCertificateTypeController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsRegistrationNumberPage(index), answers, routes.IpRightsRegistrationNumberController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsRegistrationEndPage(index), answers, routes.IpRightsRegistrationEndController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsDescriptionPage(index), answers, routes.IpRightsDescriptionController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsDescriptionWithBrandPage(index), answers, routes.IpRightsDescriptionWithBrandController.onPageLoad(NormalMode, index, answers.id)),
              PageStatus(IpRightsNiceClassPage(index, 0), answers, routes.IpRightsNiceClassController.onPageLoad(NormalMode, index, 0, answers.id))
            )
        }
      case _ =>
        Seq(PageStatus(IpRightsTypePage(0), answers, routes.IpRightsTypeController.onPageLoad(NormalMode, 0, answers.id)))
    }

  protected def additionalInformationPageStatuses(answers: UserAnswers): Seq[PageStatus[_]] =
    Seq(
      PageStatus(AdditionalInfoProvidedPage, answers, routes.AdditionalInfoProvidedController.onPageLoad(NormalMode, answers.id)),
      PageStatus(RestrictedHandlingPage, answers, routes.RestrictedHandlingController.onPageLoad(NormalMode, answers.id)),
      PageStatus(PermissionToDestroySmallConsignmentsPage, answers, routes.PermissionToDestroySmallConsignmentsController.onPageLoad(NormalMode, answers.id)),
      PageStatus(IsExOfficioPage, answers, routes.IsExOfficioController.onPageLoad(NormalMode, answers.id)),
      PageStatus(WantsOneYearRightsProtectionPage, answers, routes.WantsOneYearRightsProtectionController.onPageLoad(NormalMode, answers.id))
    )

  def continue(answers: UserAnswers): Call = {

    val pageStatuses = mainPageStatuses(answers) ++ iprPageStatuses(answers) ++ additionalInformationPageStatuses(answers)

    pageStatuses
      .find(_.cantContinue)
      .map(_.route)
      .getOrElse(routes.CheckYourAnswersController.onPageLoad(answers.id))
  }

  def noEvidence(answers: UserAnswers): Call = {
    routes.EvidenceOfPowerToActController.onPageLoad(CheckMode,answers.id)
    }
  }

final case class PageStatus[A : Reads](page: QuestionPage[A], answers: UserAnswers, route: Call) {

  def cantContinue: Boolean =
    page.isRequired(answers).contains(true) &&
    answers.get(page).isEmpty
}
