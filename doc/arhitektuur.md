# EKILEX/EVE

### Olulisemad tehnoloogiad:

- Java JDK 8 (x64 u144) 
- Apache Maven 3.5.0
- Spring Framework 4.3.10
- Spring Security 4.2.3
- Thymeleaf 3.0.7
- Hibernate 5.2.10

### Rakenduslikud moodulid:

- ekilex/ekilex-app
- ekilex/eve-app

### Tugimoodulid:

- ekilex/eki-common

### Ehitamine:

    ekilex>mvn clean install

#### Rakendamine ajutiselt Maven Tomcat pistaku abil:

    ekilex/ekilex-app>mvn tomcat7:run -P devsrvrun

    http://localhost:5555/ (html)
    http://localhost:5555/data/app (json)

    ekilex/eve-app>mvn spring-boot:run

    http://localhost:5566/ (html)
    http://localhost:5566/data/app (json)

#### Andmebaasi teenuse klasside genereerimine (kasutatakse ainult arenduses, pärast andmebaasi struktuuri muudatusi):

    ekilex>mvn validate -Dskip.jooq.generation=false

#### Kõnesüntesaatori lisamine/aktiveerimine eve-app:

eve-app toetab integreeritud, s.o. oma serverile installeeritud süntesaatorit ning EKI avaliku veebi teenust.
Konfigureerimine toimub läbi application.properties faili. Parameetrid on

- speech.synthesizer.path <- siia lisada serverile installitud süntesaatori absoluutne path
- speech.synthesizer.service.url <- teenuse url (http://heliraamat.eki.ee/syntees/koduleht.php)

Juhul, kui kumbagi parameetrit pole või on nad tühjad, on süntesaatori loogika koodis välja lülitatud. Kui olemas on mõlemad
valitakse esimesena integreeritud varjant.

ekitest.tripledev.ee serveri Jenkins-isse on konfigureeritud süntesaatori ehitamise task, mis laeb github-ist alla EKI poolt loodud süntesaatori koodi ning ehitab selle serveris valmis.

Süntesaatori käivitusparameetrid on hetkel Java koodi otse sisse kirjutatud (SpeechSynthesisService klass).
