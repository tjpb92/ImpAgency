/*
 * Ce programme importe les agences décrites dans un fichier au format XML
 * dans la base de données.
 * @version Mai 2016.
 * @author Thierry Baribaud.
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
import liba2pi.ApplicationProperties;
import liba2pi.DBServer;
import liba2pi.DBServerException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImpAgency {

    /**
     * SourceServer : prod pour le serveur de production, dev pour le serveur de
     * développement. Valeur par défaut : dev.
     */
    private String SourceServer = "dev";

    /**
     * FileIn : fichier contenant les données à charger.
     * Valeur par défaut : doit être spécifié en ligne de commande.
     */
    private String FileIn = "agences_in.xml";

    /**
     * FileOut : fichier qui recevra les résultats du chargement.
     * Valeur par défaut : agences_out.xml.
     */
    private String FileOut = "agences_out.xml";

    /**
     * debugMode : fonctionnement du programme en mode debug (true/false).
     * Valeur par défaut : false.
     */
    private boolean debugMode = false;

    /**
     * testMode : fonctionnement du programme en mode test (true/false). Valeur
     * par défaut : false.
     */
    private boolean testMode = false;

    /**
     * Les arguments en ligne de commande permettent de changer le mode de
     * fonctionnement. 
     * -i fichier : fichier dans lequel trouver les données de
     * l'agence (obligatoire). 
     * -o fichier : fichier vers lequel exporter les
     * données de l'agence (optionnel, nom par défaut agences.xml). 
     * -d : le programme fonctionne en mode débug, il est plus verbeux (optionnel). 
     * -t : le programme fonctionne en mode de test, les transactions en base de
     * données ne sont pas exécutées (optionnel).
     *
     * @param Args arguments de la ligne de commande.
     * @throws impagency.ImpAgencyException
     * @throws java.io.IOException
     * @throws liba2pi.DBServerException
     */
    public ImpAgency(String[] Args) throws ImpAgencyException, IOException, DBServerException {

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

        ApplicationProperties MyApplicationProperties;
        DBServer MyDBServer;

        MyFactoryIn = DocumentBuilderFactory.newInstance();
        MyFactoryOut = DocumentBuilderFactory.newInstance();

        // On récupère les arguments de la ligne de commande.
        System.out.println("Récupération des arguments en ligne de commande ...");
        getArgs(Args);

        System.out.println("Lecture du fichier de paramètres ...");
        MyApplicationProperties = new ApplicationProperties("ImpAgencyPublic.prop");

        System.out.println("Lecture des paramètres de base de données ...");
        MyDBServer = new DBServer(getSourceServer(),
                                  MyApplicationProperties);
        System.out.println("  " + MyDBServer);
        
       try {
            MyBuilderIn = MyFactoryIn.newDocumentBuilder();
            MyBuilderOut = MyFactoryIn.newDocumentBuilder();

            System.out.println("Lecture du fichier " + FileIn);
            MyDocumentIn = MyBuilderIn.parse(new File(FileIn));
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
            MyOutput = new StreamResult(new File(FileOut));

            // Prologue
            MyTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            MyTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            MyTransformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

            // Formatting results
            MyTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
            MyTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Output
            MyTransformer.transform(MySource, MyOutput);
            System.out.println("Fichier élément(s) à retraiter " + FileOut);

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

  /**
   * @param MySourceServer : définit le serveur source.
   */
  private void setSourceServer(String MySourceServer) {
    this.SourceServer = MySourceServer;
  }

  /**
   * @param MyFileIn : définit le fichier où prendre les données.
   */
  private void setFileIn(String MyFileIn) {
    this.FileIn = MyFileIn;
  }

  /**
   * @param MyFileOut : définit le fichier où envoyer les résultats.
   */
  private void setFileOut(String MyFileOut) {
    this.FileOut = MyFileOut;
  }

  /**
    * debugMode : fonctionnement du programme en mode debug (true/false).
   */
  private void setDebugMode(boolean myDebugMode) {
    this.debugMode = myDebugMode;
  }

  /**
    * testMode : fonctionnement du programme en mode test (true/false).
   */
  private void setTestMode(boolean myTestMode) {
    this.testMode = myTestMode;
  }

  /**
   * @return SourceServer : retourne la valeur pour le serveur source.
   */
  private String getSourceServer() {
    return(SourceServer);
  }

  /**
   * @return FileIn : retourne le nom du fichier où prendre les données.
   */
  private String getFileIn() {
    return(FileIn);
  }

  /**
   * @return FileOut : retourne le nom du fichier où envoyer les résultats.
   */
  private String getFileOut() {
    return(FileOut);
  }

  /**
   * @return daemonMode : retourne le mode de fonctionnement debug.
   */
  private boolean getDebugMode() {
    return(debugMode);
  }
  
  /**
   * @return testMode : retourne le mode de fonctionnement test.
   */
  private boolean getTestMode() {
    return(testMode);
  }
  
  /**
   * Récupère les arguments de la ligne de commande.
   * @param Args arguments de la ligne de commande.
   * @throws ImpAgencyException en cas d'erreur.
   */
    private void getArgs(String[] Args) throws ImpAgencyException {

        String[] Errmsg = {"Erreur n°1 : Mauvaise source de données",
            "Erreur n°2 : Mauvais fichier source",
            "Erreur n°3 : Mauvais fichier résultat",
            "Erreur n°4 : Mauvais argument"};
        String ErrorValue = "";
        int errNo = Errmsg.length;
        int i;
        int n;
        int ip1;

        n = Args.length;

//        System.out.println("nargs=" + n);
//    for(i=0; i<n; i++) System.out.println("args["+i+"]="+Args[i]);
        i = 0;
        while (i < n) {
//            System.out.println("args[" + i + "]=" + Args[i]);
            ip1 = i + 1;
            if (Args[i].equals("-dbserver")) {
                if (ip1 < n) {
                    if (Args[ip1].equals("dev") || Args[ip1].equals("prod") || Args[ip1].equals("mysql")) {
                        setSourceServer(Args[ip1]);
                    } else {
                        errNo = 0;
                        ErrorValue = Args[ip1];
                    }
                    i = ip1;
                } else {
                    errNo = 0;
                    ErrorValue = "non défini";
                }
            } else if (Args[i].equals("-i")) {
                if (ip1 < n) {
                    setFileIn(Args[ip1]);
                    i = ip1;
                } else {
                    errNo = 1;
                    ErrorValue = "non défini";
                }
            } else if (Args[i].equals("-o")) {
                if (ip1 < n) {
                    setFileOut(Args[ip1]);
                    i = ip1;
                } else {
                    errNo = 2;
                    ErrorValue = "non défini";
                }
            } else if (Args[i].equals("-d")) {
                setDebugMode(true);
            } else if (Args[i].equals("-t")) {
                setTestMode(true);
            } else {
                errNo = 3;
                ErrorValue = Args[i];
            }
            i++;
        }
//        System.out.println("errNo=" + errNo);
        if (errNo != Errmsg.length) {
            throw new ImpAgencyException(Errmsg[errNo] + " : " + ErrorValue);
        }
    }
   
    /**
     * Les arguments en ligne de commande permettent de changer le mode de
     * fonctionnement. 
     * -i fichier : fichier dans lequel trouver les données de
     * l'agence (obligatoire). 
     * -o fichier : fichier vers lequel exporter les
     * données de l'agence (optionnel, nom par défaut agences.xml). 
     * -d : le programme fonctionne en mode débug, il est plus verbeux (optionnel). 
     * -t : le programme fonctionne en mode de test, les transactions en base de
     * données ne sont pas exécutées (optionnel).
     *
     * @param Args arguments de la ligne de commande.
     */
    public static void main(String[] Args) {
        ImpAgency MyImpAgency;

        System.out.println("Lancement de ImpAgency ...");

        try {
            MyImpAgency = new ImpAgency(Args);
        }
        catch (Exception MyException) {
            System.out.println("Problème lors du lancement de ImpAgency" + MyException);
        }

        System.out.println("Traitement terminé.");
    }
}
