# Projet import d'agences

Le but de ce projet est de créer un programme Java permettant d'importer les agences d'un service d'urgenec au format XML dans une base de données.

##Utilisation:
```
expagncy -i fichier_entrée.xml -o fichier_sortie.xml -d -t 

-i fichier_entrée.xml est le nom du fichier qui contient les agences à charger au format XML.
-o fichier_sortie.xml est le nom du fichier qui recevra les résultats du chargement des agences au format XML.
-d le programme s'exécute en mode débug, il est beaucoup plus verbeux.
-t le programme s'exécute en mod test, les transcations en base de données ne sont pas faites.
```

##Pré-requis :
- Java 6 ou supérieur

##Format XML reconnu :
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<agences>
  <!--utopia-->
  <agence>
    <id>1234</id>
    <client>99999</client>
    <nom>utopia</nom>
    <codeAgence>UTOPIA</codeAgence>
    <appellationClient>terra incognita</appellationClient>
    <adresse>12, rue des rèves</adresse>
    <complement>bâtiment B</complement>
    <codePostal>92400</codePostal>
    <ville>UTOPIA CITY</ville>
    <email>utopia@gmail.com</email>
    <telephones>
      <telephone type="bureau">01.01.01.01.01</telephone>
      <telephone type="direct">02.02.02.02.02</telephone>
      <telephone type="fax">03.03.03.03.03</telephone>
    </telephones>
    <actif>OUI</actif>
    <debut>2016-05-16 00:00:05</debut>
    <fin>2050-12-31 23:59:59</fin>
  </agence>
</agences>
```

##Références:

[OpenClassroom java xml](https://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/dom-exemple-d-utilisation-en-java)
[Syntaxe Markdown](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

##Fichier des paramètres : 

Ce fichier permert de spécifier les paramètres d'accès aux différentes bases de données.

A adapter selon les implémentations locales.

Ce fichier est nommé : *ImpAgencyPublic.prop*
```
# Properties for production environnement
prod.dbserver.name=eole
prod.dbserver.ip=1.2.3.4
prod.dbserver.port=1234
prod.dbserver.dbname=bdd
prod.dbserver.login=user
prod.dbserver.passwd=passwd
prod.dbserver.informixserver=bdd
prod.dbserver.drivername=Informix
prod.dbserver.driverclass=com.informix.jdbc.IfxDriver
prod.dbserver.nb.thread=8

# No pre-prod dbserver for Anstel

# Properties for development environnement
dev.dbserver.name=zephir
dev.dbserver.ip=1.2.3.5
dev.dbserver.port=1235
dev.dbserver.dbname=bdd
dev.dbserver.login=user
dev.dbserver.passwd=passwd
dev.dbserver.informixserver=bdd
dev.dbserver.drivername=Informix
dev.dbserver.driverclass=com.informix.jdbc.IfxDriver
dev.dbserver.nb.thread=8

# Properties for MySQL development environnement
mysql.dbserver.name=vmsrv
mysql.dbserver.ip=localhost
mysql.dbserver.port=1234
mysql.dbserver.dbname=bdd
mysql.dbserver.login=user
mysql.dbserver.passwd=passwd
mysql.dbserver.drivername=MySQL
mysql.dbserver.driverclass=com.mysql.jdbc.Driver
mysql.dbserver.nb.thread=8
```



