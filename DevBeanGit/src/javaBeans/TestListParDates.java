package javaBeans;

import gestionErreurs.MessagesDErreurs;
import gestionErreurs.TraitementException;

public class TestListParDates {
	
	public static void main(String[] args) {
		var bop = new BOperations();
		var compteID = "123C";
		var dateInf = "2020-10-09";
		var dateSup = "2020-10-10";
		try {
			bop.ouvrirConnexion();
			bop.setCompteNumber(compteID);
			bop.setDateInf(dateInf);
			bop.setDateSup(dateSup);
			bop.listerParDates();
			var operations = bop.getOperationsParDates();
			System.out.println("Operations pour le compte \"" + compteID + "\" du " + dateInf + " au " + dateSup + " :");
			if(operations.isEmpty())  {
				System.out.println("# Aucune operation enregistree ! #");
				bop.fermerConnexion();
				return;
			}
			for(String [] operation : operations) {
				System.out.print(" =>");
				for(String field: operation) {
					System.out.print(" " + field);
				}
				System.out.println();
			}
			bop.fermerConnexion();
		} catch (TraitementException e) {
			System.out.println(MessagesDErreurs.getMessageDErreur(e.getMessage()));
		}
	}
}

// SELECT * FROM operation WHERE nocompte='123C' AND date BETWEEN '2020-10-09' AND '2020-10-10';