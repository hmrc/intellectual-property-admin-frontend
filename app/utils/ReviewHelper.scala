/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package utils

import controllers.routes
import models.{IprDetails, Mode, UserAnswers}
import play.twirl.api.HtmlFormat
import queries.{IprDetailsQuery, NiceClassIdsQuery}
import viewmodels.{DisplayAnswerRow, IprReviewRow, ReviewRow}

class ReviewHelper(userAnswers: UserAnswers) {

  def niceClassReviewRow(mode:Mode, index: Int): Option[Seq[ReviewRow]] =
    userAnswers.get(NiceClassIdsQuery(index)) map {
      niceClasses =>
        niceClasses.zipWithIndex.map {
          case (niceClass, niceClassIndex) =>
            if (niceClasses.size > 1){
              ReviewRow(
                niceClass.toString,
                Some(routes.DeleteNiceClassController.onPageLoad(mode, userAnswers.id, index, niceClassIndex)),
                routes.IpRightsNiceClassController.onPageLoad(mode, index, niceClassIndex, userAnswers.id)
              )
            } else {
              ReviewRow(
                niceClass.toString,
                None,
                routes.IpRightsNiceClassController.onPageLoad(mode, index, niceClassIndex, userAnswers.id)
              )
            }
        }
    }

  def iprReviewRow(mode: Mode): Either[DisplayAnswerRow, Seq[IprReviewRow]] =
    userAnswers.get(IprDetailsQuery) match {
      case Some(iprDetails) if iprDetails.nonEmpty =>  Right {
        for ((iprDetailsList, index) <- iprDetails.zipWithIndex)
            yield iprDetailsToRow(mode, iprDetailsList, index)
        }

      case _ => Left {
        DisplayAnswerRow("ipRightsType.noIPRights", HtmlFormat.escape(""))
        }

    }


  private val iprDetailsToRow: (Mode, IprDetails, Int) => IprReviewRow = (mode, ipr, index) =>
    IprReviewRow(
      ipr.rightsType.fold("")(t => s"ipRightsType.${t.toString}"),
      ipr.registrationNumber.getOrElse(ipr.description.getOrElse("")),
      routes.DeleteIpRightController.onPageLoad(mode, userAnswers.id, index).url,
      routes.CheckIprDetailsController.onPageLoad(mode, index, userAnswers.id).url,
      index + 1
    )
}
