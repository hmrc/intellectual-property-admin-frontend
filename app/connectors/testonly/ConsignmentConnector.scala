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

import models.Service
import play.api.Configuration
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.http.HttpReads.Implicits.{readFromJson, readRaw}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConsignmentConnector @Inject() (config: Configuration, httpClient: HttpClient) {
  private val baseUrl = config.get[Service]("microservice.services.intellectual-property")

  private val getNxtConsignmentIdUrl = s"$baseUrl/intellectual-property/consignmentId"

  private def setConsignmentUrl(consignmentId: String) = s"$baseUrl/intellectual-property/consignments/$consignmentId"

  def getNextConsignmentId()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[String] =
    httpClient.GET[String](getNxtConsignmentIdUrl)

  def submitTestOnlyConsignment(consignment: String, consignmentId: String)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[HttpResponse] =
    httpClient.PUTString[HttpResponse](
      setConsignmentUrl(consignmentId),
      consignment,
      Seq("Content-Type" -> "application/json")
    )
}
