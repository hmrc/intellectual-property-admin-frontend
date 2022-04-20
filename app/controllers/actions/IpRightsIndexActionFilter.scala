/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package controllers.actions

import com.google.inject.Inject
import models.requests.DataRequest
import pages.IpRightsPage
import play.api.mvc.{ActionFilter, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

class IpRightsIndexActionFilter(index: Int, protected val executionContext: ExecutionContext) extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    lazy val numberOfIpRights = request.userAnswers.get(IpRightsPage).getOrElse(List.empty).size

    if (index >= 0 && index <= numberOfIpRights) {
      Future.successful(None)
    } else {
      Future.successful(Some(Results.NotFound))
    }
  }
}

class IpRightsIndexActionFilterProvider @Inject()(executionContext: ExecutionContext) {

  def apply(index: Int): IpRightsIndexActionFilter =
    new IpRightsIndexActionFilter(index, executionContext)
}
