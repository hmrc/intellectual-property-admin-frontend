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

package connectors

import models.{AfaId, Lock, LockedException, Service}
import play.api.Configuration
import play.api.http.Status
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LockConnector @Inject() (
  config: Configuration,
  httpClient: HttpClient
)(implicit ec: ExecutionContext) {

  private val baseUrl = config.get[Service]("microservice.services.intellectual-property")

  private def lockable[A](implicit rds: HttpReads[A]): HttpReads[A] =
    new HttpReads[A] {

      override def read(method: String, url: String, response: HttpResponse): A =
        if (response.status == Status.LOCKED) {
          throw response.json.as[LockedException]
        } else {
          rds.read(method, url, response)
        }
    }

  def lock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId/lock"
    httpClient
      .POSTEmpty[HttpResponse](url)(lockable, implicitly, implicitly)
      .map(_ => true)
  }

  def getExistingLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[Lock]] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId/lock"
    httpClient.GET[Option[Lock]](url)(lockable, implicitly, implicitly)
  }

  def lockList(implicit hc: HeaderCarrier): Future[List[Lock]] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/list"
    httpClient.GET[List[Lock]](url)(lockable, implicitly, implicitly)
  }

  def removeLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Unit] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId"
    httpClient.DELETE[Unit](url)(lockable, implicitly, implicitly)
  }

  def replaceLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId/replace"
    httpClient.POST[AfaId, Boolean](url, afaId)(implicitly, lockable, implicitly, implicitly)
  }
}
