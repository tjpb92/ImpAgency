package impagency;

/**
 * Classe qui définit une exception lancée en cas d'erreur lors de
 * l'instanciation de la classe ImpAgency.
 *
 * @version Mai 2016.
 * @author Thierry Baribaud.
 */
class ImpAgencyException extends Exception {

    private final static String ERRMSG
            = "Problème lors de l'instanciation de ImpAgency";

    public ImpAgencyException() {
        System.out.println(ERRMSG);
    }

    public ImpAgencyException(String ErrMsg) {
        System.out.println(ERRMSG + " : " + ErrMsg);
    }
}
