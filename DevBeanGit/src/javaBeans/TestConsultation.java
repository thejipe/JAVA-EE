package javaBeans;

import gestionErreurs.MessagesDErreurs;
import gestionErreurs.TraitementException;

public class TestConsultation {
	public static void main(String[] args) {
		var bop = new BOperations();
		try {
			bop.ouvrirConnexion();
			bop.setCompteNumber("123A");
			bop.consulter();
			System.out.println("NOM = " + bop.getNom());
			System.out.println("PRENOM = " + bop.getPrenom());
			System.out.println("SOLDE = " + bop.getSolde());
			bop.fermerConnexion();
		} catch (TraitementException e) {
			System.out.println(MessagesDErreurs.getMessageDErreur(e.getMessage()));
		}
	}
}
