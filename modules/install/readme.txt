Disse katalogene/filene legges under C:\Kantega\SITENAVN (Windows)
eller /usr/local/kantega (Unix/Linux).

SITENAVN = navn på nettsted/applikasjon og må være angitt i fila
           kantega.properties som ligger under WEB-INF/classes

Dersom man ønsker å legge filene et annet sted må Java parametren
kantega.dir (-d kantega.dir="...") settes i oppstart av webtjener

conf/aksess.conf:
Her legges inn parametre som f.eks databasekopling

conf/log4j.xml:
Her legges inn konfigurasjon av logging, navn på loggfil etc

content:
Her er kataloger som filer lagres i.  Disse må settes opp som virtuelle kataloger.
Dette gjøres slik med Tomcat:
<Context path="/aksess/media" docBase="C:\Kantega\aksess\content\media" debug="0" privileged="true"/>
<Context path="/aksess/files" docBase="C:\Kantega\aksess\content\files" debug="0" privileged="true"/>

logs:
Her opprettes loggfiler fra systemet

mail:
Her ligger det maler for epostmeldinger som sendes ut av systemet.
Bør redigeres og legges inn riktige navn på nettsted osv