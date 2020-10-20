package javaBeans;

import gestionErreurs.MessagesDErreurs;
import gestionErreurs.TraitementException;

public class TestTraitement {
	public static void main(String[] args) {
		
		var bop = new BOperations();
		int value = 300;
		String operation = "-";
		try {
			bop.ouvrirConnexion();
			bop.setCompteNumber("123B"); // Elton John
			bop.setOp(operation);
			bop.setValeur(value+"");
			bop.traiter();
			System.out.println("Operation -> " + bop.getAncienSolde() + " " + operation + " " + value + "\n=> solde = " + bop.getNouveauSolde());
			bop.fermerConnexion();
		} catch (TraitementException e) {
			System.out.println(MessagesDErreurs.getMessageDErreur(e.getMessage()));
		}
		
	}
}
