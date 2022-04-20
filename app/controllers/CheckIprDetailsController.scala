/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers

import controllers.actions._
import javax.inject.Inject
import models.{AfaId, CheckMode, Mode, ModifyMode, NormalMode}
import navigation.Navigator
import pages.CheckIprDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import viewmodels.AnswerSection
import views.html.CheckIprDetailsView

class CheckIprDetailsController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           identify: IdentifierAction,
                                           getLock: LockAfaActionProvider,
                                           getData: AfaDraftDataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           validateIndex: IpRightsIndexActionFilterProvider,
                                           navigator: Navigator,
                                           val controllerComponents: MessagesControllerComponents,
                                           view: CheckIprDetailsView
                                         ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode, index: Int, afaId: AfaId): Action[AnyContent] =
    (identify andThen getLock(afaId) andThen getData(afaId) andThen requireData andThen validateIndex(index)) {
      implicit request =>

        val cyaHelper = new CheckYourAnswersHelper(request.userAnswers)

        val changeLinkMode = if (mode == NormalMode) ModifyMode else CheckMode

        val mainSection = AnswerSection(
          None,
          Seq(
            cyaHelper.ipRightsType(changeLinkMode, index),
            cyaHelper.ipRightsSupplementaryProtectionCertificateType(changeLinkMode, index),
            cyaHelper.ipRightsRegistrationNumber(changeLinkMode, index),
            cyaHelper.ipRightsRegistrationEnd(changeLinkMode, index),
            cyaHelper.ipRightsDescription(changeLinkMode, index),
            cyaHelper.ipRightsBrand(changeLinkMode, index),
            cyaHelper.ipRightsDescriptionWithBrand(changeLinkMode, index)
          ).flatten
        )

        val niceClassSection = cyaHelper.niceClasses(mode, index).map {
          niceClasses =>
            AnswerSection(
              Some("checkIprDetails.niceClasses"),
              niceClasses
            )
        }

        val nextPage = navigator.nextPage(CheckIprDetailsPage(index), mode, request.userAnswers)

        Ok(view(mode, afaId, index, mainSection, niceClassSection, nextPage))
    }
}
