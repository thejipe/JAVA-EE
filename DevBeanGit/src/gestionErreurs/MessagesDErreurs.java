package gestionErreurs;

import java.util.HashMap;

public class MessagesDErreurs {
	private final static HashMap<String, String> messages = new HashMap<>();
	static {
		messages.put("3", "Problème pour accéder à ce compte client, vérifiez qu'il est bien valide");
		messages.put("21", "Problème d'accès à la base de données, veuillez le signaler à votre administrateur");
		messages.put("22", "Problème après traitement. Le traitement a été effectué correctement mais il y a eu un problème à signaler à votre administrateur");
		messages.put("24", "Opération refusée, débit demandé supérieur au crédit du compte");
	}
	
	public static String getMessageDErreur(String id) {
		return messages.get(id);
	}
}
