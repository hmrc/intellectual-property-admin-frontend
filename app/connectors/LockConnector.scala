/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LockConnector @Inject() (
  config: Configuration,
  httpClient: HttpClientV2
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
      .post(url"$url")
      .execute[HttpResponse](lockable, ec)
      .map(_ => true)
  }

  def getExistingLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Option[Lock]] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId/lock"
    httpClient
      .get(url"$url")
      .execute[Option[Lock]](lockable, ec)
  }

  def lockList(implicit hc: HeaderCarrier): Future[List[Lock]] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/list"
    httpClient
      .get(url"$url")
      .execute[List[Lock]](lockable, ec)
  }

  def removeLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Unit] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId"
    httpClient
      .delete(url"$url")
      .execute[Unit](lockable, ec)
  }

  def replaceLock(afaId: AfaId)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/intellectual-property/draft-locks/$afaId/replace"
    httpClient
      .post(url"$url")
      .withBody(Json.toJson(afaId))
      .execute[Boolean](lockable, ec)
  }
}
