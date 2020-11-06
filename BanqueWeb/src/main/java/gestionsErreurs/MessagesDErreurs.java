package gestionsErreurs;

import java.util.HashMap;

public class MessagesDErreurs {

    private final static HashMap<String, String> messages = new HashMap<>();
    static {
        messages.put("0", "Code réservé");
        messages.put("3", "Problème pour accéder à ce compte client, vérifiez qu'il est bien valide");
        messages.put("4", "Le N° de compte doit être numérique");
        messages.put("5", "Le N° de compte doit comporter 4 caractères");
        messages.put("10", "Code réservé");
        messages.put("21", "Problème d'accès à la base de données, veuillez le signaler à votre administrateur");
        messages.put("22", "Problème après traitement. Le traitement a été effectué correctement mais il y a eu un problème à signaler à votre administrateur");
        messages.put("24", "Opération refusée, débit demandé supérieur au crédit du compte");
        messages.put("25", "La valeur doit être numérique");
        messages.put("26", "Aucune valeur n'a été saisie");
        messages.put("31", "La date initiale doit être inférieure à la date finale");
        messages.put("32", "Il n'y a eu aucune opération effectuée durant cette période");
    }

    public static String getMessageDErreur(String id) {
        return messages.get(id);
    }

}
