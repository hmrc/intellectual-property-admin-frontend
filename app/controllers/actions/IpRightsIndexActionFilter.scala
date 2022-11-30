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

package controllers.actions

import com.google.inject.Inject
import models.requests.DataRequest
import pages.IpRightsPage
import play.api.mvc.{ActionFilter, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

class IpRightsIndexActionFilter(index: Int, protected val executionContext: ExecutionContext)
    extends ActionFilter[DataRequest] {

  override protected def filter[A](request: DataRequest[A]): Future[Option[Result]] = {

    lazy val numberOfIpRights = request.userAnswers.get(IpRightsPage).getOrElse(List.empty).size

    if (index >= 0 && index <= numberOfIpRights) {
      Future.successful(None)
    } else {
      Future.successful(Some(Results.NotFound))
    }
  }
}

class IpRightsIndexActionFilterProvider @Inject() (executionContext: ExecutionContext) {

  def apply(index: Int): IpRightsIndexActionFilter =
    new IpRightsIndexActionFilter(index, executionContext)
}
