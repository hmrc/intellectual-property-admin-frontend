
# intellectual-property-admin-frontend

## Product Summary

Intellectual Property Rights Holders submit applications to HMRC to have their rights protected.

HMRC use this digital service to input these applications to a UK database, and to edit or extend existing applications.

Border Force use this digital service to access the database, find contact information for Rights Holders, and record the seizure of suspected IP-infringing goods.


## Running dependencies

Using [sm2](https://github.com/hmrc/sm2)
with the service manager profile `IPR_ALL` will start
all the Intellectual Property microservices as well as the services
that they depend on.

```
sm2 --start IPR_ALL
```

To stop the microservice from running on service manager (e.g. to run your own version locally), you can run:

```
sm2 -stop INTELLECTUAL_PROPERTY_ADMIN_FRONTEND
```


### Using localhost

To run this microservice locally on the configured port **'9876'**, you can run:

```
sbt run 
```

**NOTE:** Ensure that you are not running the microservice via service manager before starting your service locally (vice versa) or the service will fail to start


### Accessing the service

Access details can be found on
[DDCY Live Services Credentials sheet](https://docs.google.com/spreadsheets/d/1ecLTROmzZtv97jxM-5LgoujinGxmDoAuZauu2tFoAVU/edit?gid=1186990023#gid=1186990023)
for both staging and local url's or check the Tech Overview section in the
[service summary page ](https://confluence.tools.tax.service.gov.uk/display/ELSY/PIPR+Service+Summary)


## Running tests

To run this services Unit tests you can use the following command:

> `sbt test`


## Running tests

To run this services Unit tests you can use the following command:

> `sbt test`

## Scalafmt and Scalastyle

To check if all the scala files in the project are formatted correctly:
> `sbt scalafmtCheckAll`

To format all the scala files in the project correctly:
> `sbt scalafmtAll`

To check if there are any scalastyle errors, warnings or infos:
> `sbt scalastyle`


## Monitoring

The following grafana and kibana dashboards are available for this service:

* [Grafana](https://grafana.tools.production.tax.service.gov.uk/d/intellectual-property-admin-frontend/intellectual-property-admin-frontend?orgId=1&from=now-24h&to=now&timezone=browser&var-ecsServiceName=ecs-intellectual-property-admin-frontend-public-Service-wouY8SYb7Jjt&var-ecsServicePrefix=ecs-intellectual-property-admin-frontend-public&refresh=15m)
* [Kibana](https://kibana.tools.production.tax.service.gov.uk/app/dashboards#/view/intellectual-property-admin-frontend?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-15m,to:now))

## Other helpful documentation

* [Service Runbook](https://confluence.tools.tax.service.gov.uk/display/ELSY/Protect+Intellectual+Property+Rights+%28PIPR%29+Runbook)

* [Architecture Links](https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?pageId=876938617)
