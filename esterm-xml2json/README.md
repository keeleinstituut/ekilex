# esterm xml to json

Konverteerib esterm.xml faili sisu sõnakogude importeri jaoks sobivasse formaati:

(`source.json`), `dataset.json`, `word.json`, `meaning.json`, `lexeme.json` pakendatud `est.zip` faili.

Ehita kogu ekilex tarkvajaprojekt juurkataloogist:

`ekilex>mvn clean install -D skipTests`

Estermi konverteril on ainult üks Maven profiil, mis rakendub vaikimisi. Seepärast pole seda moodulit veel eraldi lisaks vaja ehitada.

Käivita konverter:

`ekilex/esterm-xml2json>mvn exec:java -D exec.args="srcfile="/path/to/esterm.xml" trgfolder="/path/to/target/folder/""`

## Märkused

- Sõnakogude importer ignoreerib `source.json` faili
- Allikad peavad ennem laetud olema ning viidete id-d asendatud
- Enne sõnakogu importimist kustuta olemasolev poolik Esterm sõnakogude haldusest
- Sõnakogude importeri praegusel tehnilisel lahendusel võtab Estermi laadimine kaua! Testimine andis kuni 20 tunnise laadimisaja!
- Mõningase võidu andis laadimisajas kui kustutada tabelite `temp_ds_import_pk_map` ja `temp_ds_import_queue` indeksid. (Lihtsaim viis on teha tabelitele drop cascade ja create ilma indeksiteta)
- Konfi `esterm-xml2json/.mvn/jvm.config` sisu vastavalt võimalustele ning kopeeri importeri kasutamise ajaks mooduli juurikasse `ekilex-app/.mvn/jvm.config`, et eraldada rakendusele rohkem ressurssi
