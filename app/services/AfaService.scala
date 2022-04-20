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

package services

import connectors.AfaConnector
import models.afa._
import models.{Address, AfaId, CompanyApplying, UserAnswers}
import pages._
import play.api.libs.json.Reads
import queries._
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DefaultAfaService @Inject()(
                            afaConnector: AfaConnector,
                            contactsService: ContactsService
                                 )(implicit ec: ExecutionContext) extends AfaService {

  private def get[A](query: Gettable[A])(implicit ua: UserAnswers, rds: Reads[A]): Option[A] = ua.get(query)

  private def buildAfa(userAnswers: UserAnswers): Option[InitialAfa] = {

    //TODO: make sure optional items have been populated (e.g. restricted handling) - does page status do this
    val endDate = LocalDate.now.plusYears(1).minusDays(1)

    for {
      additionalInfoProvided      <- additionalInfoAnswer(userAnswers)
      smallConsignments           <- userAnswers.get(PermissionToDestroySmallConsignmentsPage)
      exOfficio                   <- exOfficioAnswer(userAnswers)
      applicant                   <- getApplicantDetails(userAnswers)
      representativeContact       <- contactsService.getRepresentativeContactDetails(userAnswers)
      legalContact                <- contactsService.getLegalContactDetails(userAnswers)
      technicalContact            <- contactsService.getTechnicalContactDetails(userAnswers)
      iprs                        <- ipRights(userAnswers).filter(_.nonEmpty)
    } yield InitialAfa(
        id = userAnswers.id,
        receiptDate = userAnswers.get(ApplicationReceiptDatePage),
        additionalInfoProvided = additionalInfoProvided,
        shareWithEuropeanCommission = userAnswers.get(ShareWithEuropeanCommissionPage),
        permissionToDestroySmallConsignments = smallConsignments,
        exOfficio = exOfficio,
        applicant = applicant,
        legalContact = legalContact,
        secondaryLegalContact = contactsService.getSecondaryLegalContactDetails(userAnswers),
        technicalContact = technicalContact,
        secondaryTechnicalContact = contactsService.getSecondaryTechnicalContactDetails(userAnswers),
        ipRights = iprs,
        endDate = endDate,
        representativeContact = representativeContact,
        isRestrictedHandling = userAnswers.get(RestrictedHandlingPage)

      )
  }

  // scalastyle:off simplify.boolean.expression
  override def canCreateAfa(userAnswers: UserAnswers): Boolean ={
    val builtAfa = buildAfa(userAnswers)
    builtAfa.isDefined && builtAfa.get.representativeContact.evidenceOfPowerToAct.contains(true)
  }
  // scalastyle:on simplify.boolean.expression

  override def submit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[PublishedAfa] = {

    val afaRequest: Option[InitialAfa] = buildAfa(userAnswers)

    afaRequest.map {
      afa =>
        afaConnector.submit(afa)
    }.getOrElse(Future.failed(new Exception("Failed to create an Afa")))
  }

  private def additionalInfoAnswer(implicit answers: UserAnswers): Option[Boolean] = {
    (get(AdditionalInfoProvidedPage), get(RestrictedHandlingPage)) match {
      case (Some(false), None)      => Some(false)
      case (Some(true), Some(_))    => Some(true)
      case _                        => None
    }
  }

  private def exOfficioAnswer(implicit answers: UserAnswers): Option[Option[ExOfficio]] = {
    (get(IsExOfficioPage), get(WantsOneYearRightsProtectionPage)) match {
      case (None, _)                              => None
      case (Some(true), None)                     => None
      case (Some(true), Some(protectionResponse)) => Some(Some(ExOfficio(protectionResponse)))
      case _                                      => Some(None)
    }
  }

  private def getApplicantDetails(implicit answers: UserAnswers): Option[Company] = {
    (get(CompanyApplyingPage), get(IsCompanyApplyingUkBasedPage), getCompanyApplyingAddress, get(CompanyApplyingIsRightsHolderPage)) match {
      case (Some(CompanyApplying(name, acronym)), Some(location), Some(address), Some(applicantType)) =>
        Some(Company(name, acronym, location, address, applicantType))
      case _ => None
    }
  }

  // scalastyle:off cyclomatic.complexity
  private def ipRights(userAnswers: UserAnswers): Option[List[IpRight]] =
    for {
      ipRights <- userAnswers.get(IpRightQuery)
    } yield ipRights.map {
      case ipr: models.Trademark  =>
        Trademark(ipr.registrationNumber, ipr.registrationEnd, ipr.brand, ipr.description, ipr.niceClasses)
      case ipr: models.Copyright =>
        Copyright(ipr.description)
      case ipr: models.Design =>
        Design(ipr.registrationNumber, ipr.registrationEnd, ipr.description)
      case ipr: models.Patent =>
        Patent(ipr.registrationNumber, ipr.registrationEnd, ipr.description)
      case ipr: models.PlantVariety =>
        PlantVariety(ipr.description)
      case ipr: models.GeographicalIndication =>
        GeographicalIndication(ipr.description)
      case ipr: models.SupplementaryProtectionCertificate =>
        if(ipr.certificateType == "medicinal"){
          SupplementaryProtectionCertificate("medicinalProducts", ipr.registrationNumber, ipr.registrationEnd, ipr.description)
        } else {
          SupplementaryProtectionCertificate(ipr.certificateType, ipr.registrationNumber, ipr.registrationEnd, ipr.description)
        }
      case ipr: models.SemiconductorTopography =>
        SemiconductorTopography(ipr.description)
      case _ =>
        throw new Exception("invalid IP Right")
    }
  // scalastyle:on cyclomatic.complexity

  private def getCompanyApplyingAddress(implicit userAnswers: UserAnswers): Option[Address] = {
    get(IsCompanyApplyingUkBasedPage) flatMap {
      case true  => get(CompanyApplyingUkAddressPage)
      case false => get(CompanyApplyingInternationalAddressPage)
    }
  }


  def getDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] =
    afaConnector.getDraft(afaId).map {
      _.map {
        json =>
          json.as[UserAnswers]
      }
    }

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    afaConnector.set(userAnswers.id, userAnswers)
  }

  def removeDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    afaConnector.removeDraft(afaId)
  }

  def draftList(implicit hc: HeaderCarrier): Future[List[UserAnswers]] = {
    afaConnector.draftList
  }
}

trait AfaService {

  def canCreateAfa(userAnswers: UserAnswers): Boolean

  def submit(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[PublishedAfa]

  def set(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean]

  def getDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]

  def removeDraft(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]]

  def draftList(implicit hc: HeaderCarrier): Future[List[UserAnswers]]

}

