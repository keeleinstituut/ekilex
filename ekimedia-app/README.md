# Media app

Media app is a wrapper service for handling and linking files in AWS S3 service.

* Database: ekimedia
* Username: ekimedia
* Schema: public (default)
* Encoding: UTF8
* Collation: et_EE.UTF-8

To integrate with the service, configure environment specific application-{env}.properties files:
* ekimedia-app/src/main/resources/application.properties
  * ekimedia.service.ekilex.api.key={ekilex auth key}
* ekilex-app/src/main/resources/application.properties
  * ekimedia.service.url=https://{host}/ekimedia/api/mediafile
  * ekimedia.service.key={ekilex auth key}

Use sql script to create database table(s):
* ekimedia-app/src/main/resources/sql/create_tables.sql

Use correct environment specific profile when running application.
