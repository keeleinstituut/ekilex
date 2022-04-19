# ekilex

Sõnastiku- ja terminibaasisüsteem


Olulisemad tehnoloogiad
-----------------------

* Java JDK 8 (x64 u144) 
* Apache Maven 3.5.0
* Spring Boot 2.3.0
* Spring Framework 5.2.6
* Spring Security 5.3.2
* Thymeleaf 3.0.9
* Jooq 3.13.2
* Postgres 9.6

Rakenduslikud moodulid
----------------------

* ekilex/ekilex-app
* ekilex/wordweb-app
* ekilex/ekistat-app
* ekilex/ekilex-etl (aegunud)
* ekilex/eve-app (aegunud)

Tugimoodulid
------------

* ekilex/eki-common
* ekilex/ekilex-rus-morph

Ehitamine
---------

`ekilex>mvn clean install`

Veebirakenduste käivitamine Spring Boot Tomcat pistaku abil
-----------------------------------------------------------

`ekilex/ekilex-app>mvn spring-boot:run`

`ekilex/ekilex-app>mvn spring-boot:run -Dspring-boot.run.profiles=dev`

http://localhost:5555/ (html)

http://localhost:5555/api/* (json)

`ekilex/wordweb-app>mvn spring-boot:run`

`ekilex/wordweb-app>mvn spring-boot:run -Dspring-boot.run.profiles=dev`

http://localhost:5577/ (html)

Muud tehn operatsioonid
-----------------------

Andmebaasi tabelite proksi-klasside genereerimine (kasutatakse ainult arenduses, pärast andmebaasi struktuuri muudatusi):

`ekilex>mvn compile -D skip.jooq.generation=false`

Detailsemalt kõigest:
* [paigaldusjuhend](doc/ekilex-paigaldusjuhend.pdf)
* [ekilex wiki](https://github.com/tripledev/ekilex/wiki)

