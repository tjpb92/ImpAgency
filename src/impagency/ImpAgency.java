/*
 * This programme that imports agencies from a file in xml format
 * into the database.
 * @version May 2016
 * @author Thierry Baribaud
 */
package impagency;

import agency.Fagency;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImpAgency {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Fagency MyFagency;

        DocumentBuilderFactory MyFactoryIn;
        DocumentBuilder MyBuilderIn;
        Document MyDocumentIn;

        DocumentBuilderFactory MyFactoryOut;
        DocumentBuilder MyBuilderOut;
        Document MyDocumentOut;

        Element MyRacine;
        Element MyAgencies;
        Element MyAgencyOut;
        NodeList MyRacineNoeuds;
        int nbRacineNoeuds;
        Node OneNode;
        Element OneAgency;

        NodeList MyNodeList;
        Element MyElementIn;
        Element MyElementOut;
        Element MyError;
        String MyString;
        String MyType;
        int count;
        TimeZone MyTimeZone = TimeZone.getTimeZone("Europe/Paris");
        DateFormat MyDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp Now = new Timestamp(new java.util.Date().getTime());
        Timestamp Future = Timestamp.valueOf("2050-12-31 23:59:59.0");
        Timestamp MyTimestamp;
        int nbError;
        int nbWarning;
        boolean error;
        boolean warning;
        String WarningMsg;
        String ErrorMsg;
        String ElementName;
        int nbAgency = 0;
        Element Phones;
        Comment MyComment;

        TransformerFactory MyTransformerFactory;
        Transformer MyTransformer;
        DOMSource MySource;
        StreamResult MyOutput;

        String FilenameIn = "agences.xml";
        String FilenameOut = "ImpAgencyResults.xml";

        MyFactoryIn = DocumentBuilderFactory.newInstance();
        MyFactoryOut = DocumentBuilderFactory.newInstance();
        try {
            MyBuilderIn = MyFactoryIn.newDocumentBuilder();
            MyBuilderOut = MyFactoryIn.newDocumentBuilder();

            System.out.println("Lecture du fichier " + FilenameIn);
            MyDocumentIn = MyBuilderIn.parse(new File(FilenameIn));
            MyDocumentOut = MyBuilderOut.newDocument();

            //Affiche la version de XML
            System.out.println("Version XML=" + MyDocumentIn.getXmlVersion());

            //Affiche l'encodage
            System.out.println("Encodage=" + MyDocumentIn.getXmlEncoding());

            //Affiche s'il s'agit d'un document standalone		
            System.out.println("Standalone=" + MyDocumentIn.getXmlStandalone());

            MyRacine = MyDocumentIn.getDocumentElement();
            System.out.println("Nom de la racine : " + MyRacine.getNodeName());
            MyAgencies = MyDocumentOut.createElement(MyRacine.getNodeName());
            MyDocumentOut.appendChild(MyAgencies);

            MyRacineNoeuds = MyRacine.getChildNodes();

            nbRacineNoeuds = MyRacineNoeuds.getLength();
            System.out.println("Nombre de noeuds : " + nbRacineNoeuds);

            for (int i = 0; i < nbRacineNoeuds; i++) {
                if (MyRacineNoeuds.item(i).getNodeType() == Node.COMMENT_NODE) {
                    System.out.println(MyRacineNoeuds.item(i).getNodeName());
                    OneNode = MyRacineNoeuds.item(i);
                    System.out.println("  " + OneNode.getTextContent());
                    MyComment = MyDocumentOut.createComment(OneNode.getTextContent());
                    MyAgencies.appendChild(MyComment);
                } else if (MyRacineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    nbAgency++;
                    System.out.println(MyRacineNoeuds.item(i).getNodeName() + " no " + nbAgency);
                    OneNode = MyRacineNoeuds.item(i);
//                    System.out.println(OneNode.getNodeName());
                    MyAgencyOut = MyDocumentOut.createElement(OneNode.getNodeName());
                    MyAgencies.appendChild(MyAgencyOut);

                    OneAgency = (Element) OneNode;
                    MyFagency = new Fagency();
                    nbError = 0;
                    nbWarning = 0;

                    // Récupère l'identifiant de l'agence
                    ElementName = "id";
                    error = false;
                    ErrorMsg = "L'identifiant de l'agence doit être numérique";
                    warning = false;
                    WarningMsg = "Identifiant non défini";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " identifiant(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        if (MyString.length() > 0) {
                            try {
                                MyFagency.setA6num(Integer.parseInt(MyString));
                            } catch (Exception MyException) {
                                nbError++;
                                error = true;
                            }
                        } else {
                            nbWarning++;
                            warning = true;
                        }
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère le client dont dépend l'agence
                    ElementName = "client";
                    error = false;
                    ErrorMsg = "L'identifiant du client doit être numérique";
                    warning = false;
                    WarningMsg = "";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " client(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        if (MyString.length() > 0) {
                            try {
                                MyFagency.setA6unum(Integer.parseInt(MyString));
                            } catch (Exception MyException) {
                                nbError++;
                                error = true;
                            }
                        } else {
                            nbError++;
                            error = true;
                            ErrorMsg = "Identifiant client non défini";
                        }
                    } else {
                        nbError++;
                        error = true;
                        ErrorMsg = "Identifiant client non défini";
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère le nom de l'agence
                    ElementName = "nom";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Nom d'agence non défini";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " nom(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6name(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère le nom abrégé de l'agence
                    ElementName = "codeAgence";
                    error = false;
                    ErrorMsg = "Code agence non défini";
                    warning = false;
                    WarningMsg = "";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " nom(s) abrégé(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        if (MyString.length() > 0) {
                            MyFagency.setA6abbname(MyString);
                        } else {
                            nbError++;
                            error = true;
                        }
                    } else {
                        nbError++;
                        error = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère l'appellation de l'agence
                    ElementName = "appellationClient";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Appellation non définie";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " appellation(s) trouvée(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6extname(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère l'adresse de l'agence
                    ElementName = "adresse";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Adresse non définie";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " adresse(s) trouvée(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6daddress(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère le complément d'adresse de l'agence
                    ElementName = "complement";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Complément d'adresse non défini";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " complément(s) adresse(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6daddress2(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère le code postal de l'agence
                    ElementName = "codePostal";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Code postal non défini";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " code(s) postal/aux trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6dposcode(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère la ville de l'agence
                    ElementName = "ville";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Ville non définie";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " ville(s) trouvée(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6dcity(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    Phones = MyDocumentOut.createElement("telephones");
                    MyAgencyOut.appendChild(Phones);

                    // Récupére les téléphones de l'agence
                    ElementName = "telephone";
                    ErrorMsg = "";
                    WarningMsg = "Type de téléphone inconnu";
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " téléphone(s) trouvé(s)");
                    for (int j = 0; j < count; j++) {
                        error = false;
                        warning = false;
                        MyElementOut = MyDocumentOut.createElement(ElementName);
                        MyAgencyOut.appendChild(MyElementOut);
                        MyElementIn = (Element) MyNodeList.item(j);
                        MyType = MyElementIn.getAttribute("type");
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyElementOut.setAttribute("type", MyType);
                        if (MyType.equals("bureau")) {
                            MyFagency.setA6teloff(MyString);
                        } else if (MyType.equals("direct")) {
                            MyFagency.setA6teldir(MyString);
                        } else if (MyType.equals("fax")) {
                            MyFagency.setA6telfax(MyString);
                        } else {
                            nbWarning++;
                            warning = true;
                        }
                        Phones.appendChild(MyElementOut);
                        if (error) {
                            MyElementOut.setAttribute("erreur", ErrorMsg);
                            System.out.println("  ERREUR : " + ErrorMsg);
                        } else if (warning) {
                            MyElementOut.setAttribute("warning", WarningMsg);
                            System.out.println("  WARNING : " + WarningMsg);
                        }
                    }

                    // Récupère l'email de l'agence
                    ElementName = "email";
                    error = false;
                    ErrorMsg = "";
                    warning = false;
                    WarningMsg = "Email non défini";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " email(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        MyFagency.setA6email(MyString);
                    } else {
                        nbWarning++;
                        warning = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère l'activité de l'agence
                    MyFagency.setA6active(0);
                    ElementName = "actif";
                    error = false;
                    ErrorMsg = "Code activité erronné";
                    warning = false;
                    WarningMsg = "";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " actif(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        if (MyString.equals("OUI")) {
                            MyFagency.setA6active(1);
                        } else if (MyString.equals("NON")) {
                            MyFagency.setA6active(0);
                        } else {
                            nbError++;
                            error = true;
                        }
                    } else {
                        nbError++;
                        error = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère la date de début d'activité de l'agence
                    MyFagency.setA6begactive(Now);
                    ElementName = "debut";
                    error = false;
                    ErrorMsg = "Date de début d'activite erronnée";
                    warning = false;
                    WarningMsg = "";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " début(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        try {
                            MyTimestamp = Timestamp.valueOf(MyString);
                            MyFagency.setA6begactive(MyTimestamp);
                        } catch (Exception MyException) {
                            nbError++;
                            error = true;
                        }
                    } else {
                        nbError++;
                        error = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    // Récupère la date de fin d'activité de l'agence
                    MyFagency.setA6begactive(Future);
                    ElementName = "fin";
                    error = false;
                    ErrorMsg = "Date de fin d'activite erronnée";
                    warning = false;
                    WarningMsg = "";
                    MyElementOut = MyDocumentOut.createElement(ElementName);
                    MyAgencyOut.appendChild(MyElementOut);
                    MyNodeList = OneAgency.getElementsByTagName(ElementName);
                    count = MyNodeList.getLength();
//                    System.out.println("  " + count + " fin(s) trouvé(s)");
                    if (count > 0) {
                        MyElementIn = (Element) MyNodeList.item(0);
                        MyString = MyElementIn.getTextContent();
                        System.out.println("  " + ElementName + "=" + MyString);
                        MyElementOut.appendChild(MyDocumentOut.createTextNode(MyString));
                        try {
                            MyTimestamp = Timestamp.valueOf(MyString);
                            MyFagency.setA6endactive(MyTimestamp);
                        } catch (Exception MyException) {
                            nbError++;
                            error = true;
                        }
                    } else {
                        nbError++;
                        error = true;
                    }
                    if (error) {
                        MyElementOut.setAttribute("erreur", ErrorMsg);
                        System.out.println("  ERREUR : " + ErrorMsg);
                    } else if (warning) {
                        System.out.println("  WARNING : " + WarningMsg);
                    }

                    //TODO : Check activity against [begin, end[
                    //TODO : Add error message for every element and generate XML file.
                    System.out.println("  MyFagency=" + MyFagency);
                    System.out.println("  " + nbError + " error(s), " + nbWarning + " warning(s)");

                }
            }
            MyTransformerFactory = TransformerFactory.newInstance();
            MyTransformer = MyTransformerFactory.newTransformer();

            MySource = new DOMSource(MyDocumentOut);
            MyOutput = new StreamResult(new File(FilenameOut));

            // Prologue
            MyTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            MyTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            MyTransformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            // Formatting results
            MyTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            MyTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Output
            MyTransformer.transform(MySource, MyOutput);
            System.out.println("Fichier élément(s) à retraiter " + FilenameOut);

        } catch (ParserConfigurationException MyException) {
            Logger.getLogger(ImpAgency.class.getName()).log(Level.SEVERE, null, MyException);
        } catch (SAXException MyException) {
            Logger.getLogger(ImpAgency.class.getName()).log(Level.SEVERE, null, MyException);
        } catch (IOException MyException) {
            Logger.getLogger(ImpAgency.class.getName()).log(Level.SEVERE, null, MyException);
        } catch (TransformerConfigurationException MyException) {
            Logger.getLogger(ImpAgency.class.getName()).log(Level.SEVERE, null, MyException);
            System.out.println("Problem configuring XML document " + MyException);
        } catch (TransformerException MyException) {
            Logger.getLogger(ImpAgency.class.getName()).log(Level.SEVERE, null, MyException);
            System.out.println("Problem writing XML document " + MyException);
        }

    }
}
