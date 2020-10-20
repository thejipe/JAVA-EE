package javaBeans;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import gestionErreurs.TraitementException;

public class BOperations {
	private String noDeCompte;
	private String nom;
	private String prenom;
	private BigDecimal solde;
	private BigDecimal ancienSolde;
	private BigDecimal nouveauSolde;
	private BigDecimal valeur;
	private String op;
	private String dateInf;
	private String dateSup;
	private ArrayList<String []> operationsParDates;
	private Connection connection;
	private ResultSet resultSet;
	
	public String getDateInf() {
		return dateInf;
	}

	public void setDateInf(String dateInf) {
		if(Objects.requireNonNull(dateInf).isBlank()) {
			throw new IllegalArgumentException();
		}	
		this.dateInf = dateInf;
	}

	public String getDateSup() {
		return dateSup;
	}

	public void setDateSup(String dateSup) {
		if(Objects.requireNonNull(dateSup).isBlank()) {
			throw new IllegalArgumentException();
		}
		this.dateSup = dateSup;
	}

	public ArrayList<String[]> getOperationsParDates() {
		return operationsParDates;
	}
	
	public void setCompteNumber(String compteNumber) {
		if(Objects.requireNonNull(compteNumber).isBlank()) {
			throw new IllegalArgumentException();
		}
		this.noDeCompte = compteNumber;
	}
	
	public void setOp(String op) {
		if(Objects.requireNonNull(op).isBlank()) {
			throw new IllegalArgumentException();
		}
		this.op = op;
	}
	
	public void setValeur(String valeur) {
		if(Objects.requireNonNull(valeur).isBlank()) {
			throw new IllegalArgumentException();
		}
		this.valeur = new BigDecimal(valeur);
	}
	
	public BigDecimal getAncienSolde() {
		return ancienSolde;
	}

	public BigDecimal getNouveauSolde() {
		return nouveauSolde;
	}

	public String getValeur() {
		return valeur.toString();
	}

	public void ouvrirConnexion() throws TraitementException {
		try {
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/BANQUE?serverTimezone=UTC",
					"root",
					"Jp22102810"
					);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TraitementException("21");
		}
	}
	
	public void fermerConnexion() throws TraitementException {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TraitementException("22");
		}
	}
	
	public void consulter() throws TraitementException {
		try {
			var statment = connection.createStatement();
			resultSet = statment.executeQuery("SELECT * FROM compte WHERE nocompte='" + noDeCompte+ "';");
			resultSet.next();
			nom = resultSet.getString("nom");
			prenom = resultSet.getString("prenom");
			solde = resultSet.getBigDecimal("solde");
			statment.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TraitementException("3");
		}
	}
	
	public void traiter() throws TraitementException {
		try {
			var statment = connection.createStatement();
			resultSet = statment.executeQuery("SELECT solde FROM compte WHERE nocompte='" + noDeCompte+ "';");
			resultSet.next();
			ancienSolde = new BigDecimal(resultSet.getString("solde"));
			switch(op) {
			case "+":
				nouveauSolde = new BigDecimal(ancienSolde.toString()).add(valeur);
				break;
				
			case "-":
				nouveauSolde = new BigDecimal(ancienSolde.toString()).subtract(valeur);
				break;
				
			default:
				System.out.println("Unrecognized opération !");
				resultSet.close();
				statment.close();
				return;
			}
			
			if(nouveauSolde.compareTo(new BigDecimal(0)) == -1) {
				System.out.println("Operation refusé, votre solde est insuffisant...");
				resultSet.close();
				statment.close();
				return;
			}
			statment.executeUpdate("UPDATE compte "
					+ "SET solde = '" + nouveauSolde + "' "
					+ "WHERE nocompte = '" + noDeCompte + "';");
			statment.execute("INSERT INTO operation (nocompte, date, heure, op, valeur) VALUES "
					+ "('" + noDeCompte + "', CURDATE(), CURTIME(), '" + op + "', '" + valeur + "');");
			resultSet.close();
			statment.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TraitementException("24");
		}
	}
	
	public void listerParDates() throws TraitementException {
		try {
			var statment = connection.createStatement();
			resultSet = statment.executeQuery("SELECT date, op, valeur FROM operation WHERE "
					+ "nocompte='" + noDeCompte + "' AND date BETWEEN '" + dateInf + "' AND '" + dateSup + "';");
			operationsParDates = new ArrayList<>();
			while(resultSet.next()) {
				var operation = new String[3];
				operation[0] = resultSet.getString("date");
				operation[1] = resultSet.getString("op");
				operation[2] = resultSet.getString("valeur");
				operationsParDates.add(operation);
			}
			resultSet.close();
			statment.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new TraitementException("3");
		}
	}
	
	public String getNom() {
		return nom;
	}
	
	public String getPrenom() {
		return prenom;
	}
	
	public BigDecimal getSolde() {
		return solde;
	}
	
}
