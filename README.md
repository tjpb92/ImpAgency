# Projet import d'agences

Le but de ce projet est de cr�er un programme Java permettant d'importer les agences d'un service d'urgence au format XML dans une base de donn�es.

##Utilisation:
```
java ImpAgency -i fichier_entr�e.xml -o fichier_sortie.xml [-d] [-t] 
```
o� :
* ```-i fichier_entr�e.xml``` est le nom du fichier qui contient les agences � charger au format XML (param�tre obligatoire).
* ```-o fichier_sortie.xml``` est le nom du fichier qui recevra les r�sultats du chargement des agences au format XML (param�tre obligatoire).
* ```-d``` le programme s'ex�cute en mode d�bug, il est beaucoup plus verbeux. D�sactiv� par d�faut (param�tre optionnel).
* ```-t``` le programme s'ex�cute en mode test, les transcations en base de donn�es ne sont pas faites. D�sactiv� par d�faut (param�tre optionnel).

##Pr�-requis :
- Java 6 ou sup�rieur.
- JDBC Informix
- JDBC MySql

##Format XML reconnu :
```
<?xml version="1.0" encoding="ISO-8859-15" standalone="no"?>
<!DOCTYPE agences SYSTEM "agences.dtd">

<agences>
  <!--utopia-->
  <agence>
    <id>1234</id>
    <client>99999</client>
    <nom>utopia</nom>
    <codeAgence>UTOPIA</codeAgence>
    <appellationClient>terra incognita</appellationClient>
    <adresse>12, rue des r�ves</adresse>
    <complement>b�timent B</complement>
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

##DTD officielle :

Nom du fichier : *agences.dtd*

```
<!-- Racine -->
<!ELEMENT agences (agence*)>

<!-- agence -->
<!ELEMENT agence (ids?, noms?, adressePostale?, email?, telephones?, etat?)>

<!-- bloc IDs -->
<!ELEMENT ids (id?, idClient, codeAgence)>
<!ELEMENT id (#PCDATA)>
<!ELEMENT idClient (#PCDATA)>
<!ELEMENT codeAgence (#PCDATA)>

<!-- Bloc noms -->
<!ELEMENT noms (nom?, appellationClient?)>
<!ELEMENT nom (#PCDATA)>
<!ELEMENT appellationClient (#PCDATA)>

<!-- Bloc adressePostale -->
<!ELEMENT adressePostale (adresse?, complement?, codePostal?, ville?)>
<!ELEMENT adresse (#PCDATA)>
<!ELEMENT complement (#PCDATA)>
<!ELEMENT codePostal (#PCDATA)>
<!ELEMENT ville (#PCDATA)>

<!-- Email -->
<!ELEMENT email (#PCDATA)>

<!-- Bloc telephones -->
<!ELEMENT telephones (telephone*)>
<!ELEMENT telephone (#PCDATA)>
<!ATTLIST telephone type (bureau | direct | fax) #REQUIRED>

<!-- Bloc etat -->
<!ELEMENT etat (actif?, debut?, fin?)>
<!ELEMENT actif (#PCDATA)>
<!ELEMENT debut (#PCDATA)>
<!ELEMENT fin (#PCDATA)>
```

##Fichier des param�tres : 

Ce fichier permet de sp�cifier les param�tres d'acc�s aux diff�rentes bases de donn�es.

A adapter selon les impl�mentations locales.

Ce fichier est nomm� : *MyDatabases.prop*.

Le fichier *MyDatabases_Example.prop* est fourni � titre d'exemple.

##R�f�rences:

- Construire une application XML, J.C. Bernadac, F. Knab, Eyrolles.

- [OpenClassroom Java XML](https://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/dom-exemple-d-utilisation-en-java)
- [Syntaxe Markdown](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)
- [Tuto OpenClassroom sur DTD](https://openclassrooms.com/courses/structurez-vos-donnees-avec-xml/introduction-aux-definitions-et-aux-dtd)
- [Tuto W3C sur DTD (en)](https://www.google.fr/url?sa=t&rct=j&q=&esrc=s&source=web&cd=1&cad=rja&uact=8&sqi=2&ved=0ahUKEwiDrurll-fMAhWHBsAKHYdzAegQFggfMAA&url=http%3A%2F%2Fwww.w3schools.com%2Fxml%2Fxml_dtd_intro.asp&usg=AFQjCNGCt7X2oRyUSkTES1aXf8GljqhekA&bvm=bv.122448493,d.ZGg)
- [Validation fichier XML](http://www.xmlvalidation.com/)
- [Convertisseur DTD/XSD](http://www.freeformatter.com/xsd-generator.html)
- [Tuto XML/XSD](http://www.codeguru.com/java/article.php/c13529/XSD-Tutorial-XML-Schemas-For-Beginners.htm)

