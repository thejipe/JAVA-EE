package gestionErreurs;

import java.util.HashMap;

public class MessagesDErreurs {
	private final static HashMap<String, String> messages = new HashMap<>();
	static {
		messages.put("3", "Probl�me pour acc�der � ce compte client, v�rifiez qu'il est bien valide");
		messages.put("21", "Probl�me d'acc�s � la base de donn�es, veuillez le signaler � votre administrateur");
		messages.put("22", "Probl�me apr�s traitement. Le traitement a �t� effectu� correctement mais il y a eu un probl�me � signaler � votre administrateur");
		messages.put("24", "Op�ration refus�e, d�bit demand� sup�rieur au cr�dit du compte");
	}
	
	public static String getMessageDErreur(String id) {
		return messages.get(id);
	}
}
