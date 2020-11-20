package javaBeans;

import gestionsErreurs.TraitementException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/**
 * Represent the data base intermediary of the web application.
 * Some methods are available to provide tipical actions expected on the data base "BANQUE".
 */
public class BOperations {

    private String noCompte, nom, prenom, op, dateInf, dateSup;
    private BigDecimal solde, ancienSolde, nouveauSolde, valeur;
    private ArrayList<String[]> operationsParDates;
    private Connection connect;

    /**
     * Set the account number used for the next transactions.
     * @param noCompte String that represent the account number to use
     */
    public void setNoCompte(String noCompte) {
        this.noCompte = noCompte;
    }

    /*
    public void ouvrirConnexion() {
        try {
            connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/BANQUE?serverTimezone=UTC","root", "admin");
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }*/

    /**
     * Provide access to the associated data base, by opening a connection with this one.
     */
    public void ouvrirConnexion(DataSource ds) throws TraitementException {
        try {
            connect = ds.getConnection();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e);
            throw new TraitementException("21");
        }
    }

    /**
     * Close the opened connection to the data base.
     */
    public void fermerConnexion() throws TraitementException {
        try {
            connect.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e.getMessage());
            throw new TraitementException("22");
        }
    }

    /**
     * Provide the consultation process to the data associated to the setted account number.
     * The fields "solde", "nom", "prenom" are suposed available before this call.
     *
     * @throws TraitementException If no account was defined with the specified account number,
     * or to secify an error during the data base access.
     */
    public void consulter() throws TraitementException {
        try (Statement statmt = connect.createStatement()) {
            ResultSet res = statmt.executeQuery("SELECT * FROM COMPTE WHERE noCompte=" + noCompte + ";");
            if (!res.next())
                throw new TraitementException("3");
            nom = res.getString("nom");
            prenom = res.getString("prenom");
            solde = res.getBigDecimal("solde");
            res.close();
        } catch (SQLException error) {
            throw new TraitementException("21");
        }
    }

    /**
     * Process the traitment operation, in the account,
     * by using the setted "noCompte", "value" and "op" fields.
     * 
     * @throws TraitementException if there is any problem with the data base access,
     * or if the account balance doesn't permit the specified operation
     */
    public void traiter() throws TraitementException {
        Statement statmt = null;
        try {
            statmt = connect.createStatement();
            ResultSet res = statmt.executeQuery("SELECT solde FROM COMPTE WHERE noCompte=" + noCompte + ";");
            res.next();
            ancienSolde = res.getBigDecimal("solde");
            switch (op) {
                case "+": nouveauSolde = new BigDecimal(ancienSolde.toString()).add(valeur); break;
                case "-": nouveauSolde = new BigDecimal(ancienSolde.toString()).subtract(valeur); break;
                default:
                    System.out.println("ERROR TRAITEMENT !");
                    return;
            }
            res.close();
            if (nouveauSolde.compareTo(new BigDecimal(0)) >= 0) {
                statmt.execute("UPDATE COMPTE SET solde = " + nouveauSolde + " WHERE noCompte = " + noCompte + ";");
                statmt.execute("INSERT INTO OPERATIONS(noCompte, date, heure, op, valeur) VALUES ('"+noCompte+"',CURRENT_DATE,CURRENT_TIME,'"+op+"',"+valeur+");");
            } else
                throw new TraitementException("24");
        } catch (SQLException e) {
            System.out.println("SQLException " + e.getMessage());
            throw new TraitementException("22");
        }
    }

    /**
     * Provide all operations that happend in the account between the setted "dateInf" and dateSup" fields.
     *
     * @throws TraitementException if there is any problem to access the account data
     */
    public void listerParDates() throws TraitementException {
        try {
            var statment = connect.createStatement();
            var result = statment.executeQuery("SELECT date, op, valeur FROM OPERATIONS WHERE noCompte = '" + noCompte + "' AND date BETWEEN '" + dateInf + "' AND '" + dateSup + "';");

            operationsParDates = new ArrayList<>();
            while(result.next()) {
                var operation = new String[3];
                operation[0] = result.getString("date");
                operation[1] = result.getString("op");
                operation[2] = result.getString("valeur");
                operationsParDates.add(operation);
            }
            result.close();
            statment.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e.getMessage());
            throw new TraitementException("3");
        }
    }

    /**
     * Provide the value of the field "dateInf" used in the listing of operations.
     * @return the value of the associated field, or null if it's unset
     */
    public String getDateInf() {
        return dateInf;
    }

    /**
     * Used to set the value of the "dateInf" field used in the listing of operations.
     * @param dateInf the value used to set the "dateInf" field, in the form "JJ-MM-AAAA"
     */
    public void setDateInf(String dateInf) {
        this.dateInf = dateInf;
    }

    /**
     * Provide the value of the field "dateSup" used in the listing of operations.
     * @return the value of the associated field, or null if it's unset
     */
    public String getDateSup() {
        return dateSup;
    }

    /**
     * Used to set the value of the "dateSup" field used in the listing of operations.
     * @param dateSup the value used to set the "dateSup" field, in the form "JJ-MM-AAAA"
     */
    public void setDateSup(String dateSup) {
        this.dateSup = dateSup;
    }

    /**
     * Provide the list of operations associated with the last call to the "listerParDates" method.
     * @return the list of operations between "dateInf" and "dateSup" fields
     */
    public ArrayList<String[]> getOperationsParDates() {
        return operationsParDates;
    }

    /**
     * Used to set the value of the "op" field used in the traitment process.
     * @param op the value used to set "op" field that must be "-" or "+"
     */
    public void setOp(String op) {
        this.op = op;
    }

    /**
     * Used to set the value of the "valeur" field used in the traitment process.
     * @param valeur the value used to set "valeur" field that must be a numeric value
     */
    public void setValeur(String valeur) {
        this.valeur = new BigDecimal(valeur);
    }

    /**
     * Provide the value of the "op" field used in the traitmet process.
     * @return the value of the associated filed, that must be "-" or "+"
     */
    public String getOp() {
        return op;
    }

    /**
     * Provide the previous account balance, before the last call to the "listerParDates" method.
     * @return the value of the associated field that represent the previous account balance
     */
    public BigDecimal getAncienSolde() {
        return ancienSolde;
    }

    /**
     * Provide the current account balance, after the last call to the "listerParDates" method.
     * @return the value of the associated field that represent the current account balance
     */
    public BigDecimal getNouveauSolde() {
        return nouveauSolde;
    }

    /**
     * Provide the current value of the field "valeur" that will be used in the next call to the
     * "listerParDates" method to determinate the new account balance.
     * @return the value of the associated field that represent the value used in the traitment process
     */
    public String getValeur() {
        return valeur.toString();
    }

    /**
     * Provide the current account number used in each method that exchange with the data base.
     * @return the value of the associated field that represent the current account number
     */
    public String getNoCompte() {
        return noCompte;
    }

    /**
     * Provide the last name of the current account owner, set after a call to the consultation process method.
     * @return {@String} that represent the last name of the current account owner
     */
    public String getNom() {
        return nom;
    }

    /**
     * Provide the first name of the current account owner, set after a call to the consultation process method.
     * @return {@String} that represent the first name of the current account owner
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Provide the account balance of the current account, set after a call to the consultation process method.
     * @return {@BigDecimal} that represent the account balance of the current account
     */
    public BigDecimal getSolde() {
        return solde;
    }
}
