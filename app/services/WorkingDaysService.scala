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

package services

import config.Service
import models.Region
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.http.client.HttpClientV2

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http._

class WorkingDaysService @Inject() (config: Configuration, httpClient: HttpClientV2) extends HttpErrorFunctions {

  private val baseUrl = config.get[Service]("microservice.services.intellectual-property")

  private val jsonReads: Reads[LocalDate] =
    (__ \ "date").read[LocalDate]

  private def asDate: HttpReads[LocalDate] =
    new HttpReads[LocalDate] {

      override def read(method: String, url: String, response: HttpResponse): LocalDate =
        if (response.status == Status.OK) {
          response.json
            .validate[LocalDate](jsonReads)
            .fold(
              errors => throw new JsValidationException(method, url, classOf[LocalDate], errors.toString()),
              valid => valid
            )
        } else {
          throw new Exception(s"HTTP call failed with return code ${response.status}")
        }
    }

  def workingDays(region: Region, from: LocalDate, numberOfDays: Int)(implicit
    ec: ExecutionContext,
    hc: HeaderCarrier
  ): Future[LocalDate] = {

    val url = s"$baseUrl/intellectual-property/working-days/$region/$from/$numberOfDays"
    httpClient.get(url"$url").execute[LocalDate](asDate, implicitly)
  }
}
