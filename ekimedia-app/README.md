# Stat app

Stat app is API module for collecting and sharing statistics data.

Create 'login role' and database 

* Database: ekistat
* Username: ekistat
* Schema: public (default)
* Encoding: UTF8
* Collation: et_EE.UTF-8

Configure environment specific application.properties files:
* ekistat-app/src/main/resources/application.properties
* wordweb-app/src/main/resources/application.properties
* ekilex-app/src/main/resources/application.properties

Use sql script to create database tables:
* ekistat-app/src/main/resources/sql/create_tables.sql

Use correct environment specific profile when running application.