/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import com.google.inject.Inject
import models.requests.DataRequest
import pages.AllIpRightsNiceClasses
import play.api.mvc.{ActionFilter, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

class NiceClassIndexActionFilter(
                                  iprIndex: Int,
                                  niceClassIndex: Int,
                                  protected val executionContext: ExecutionContext
                                ) extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    lazy val numberOfIpRights = request.userAnswers.get(AllIpRightsNiceClasses(iprIndex)).getOrElse(List.empty).size

    if (niceClassIndex >= 0 && niceClassIndex <= numberOfIpRights) {
      Future.successful(None)
    } else {
      Future.successful(Some(Results.NotFound))
    }
  }
}

class NiceClassIndexActionFilterProvider @Inject()(executionContext: ExecutionContext) {

  def apply(iprIndex: Int, niceClassIndex: Int): NiceClassIndexActionFilter =
    new NiceClassIndexActionFilter(iprIndex, niceClassIndex, executionContext)
}

