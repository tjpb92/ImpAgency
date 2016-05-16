# Projet import d'agences

Le but de ce projet est de créer un programme Java permettant d'importer les agences d'un service d'urgenec au format XML dans une base de données.

Utilisation:
```
expagncy -i fichier_entrée.xml -o fichier_sortie.xml -d -t 

-i fichier_entrée.xml est le nom du fichier qui contient les agences à charger au format XML.
-o fichier_sortie.xml est le nom du fichier qui recevra les résultats du chargement des agences au format XML.
-d le programme s'exécute en mode débug, il est beaucoup plus verbeux.
-t le programme s'exécute en mod test, les transcations en base de données ne sont pas faites.
```

Pré-requis :
- Java 6 ou supérieur

Format XML reconnu :
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

Références:

[OpenClassroom java xml](https://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/dom-exemple-d-utilisation-en-java)

