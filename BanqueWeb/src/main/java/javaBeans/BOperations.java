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
     * Provide access to the associated data base. By opening a connection with this one.
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
            System.out.println(e);
            throw new TraitementException("22");
        }
    }

    /**
     * Provide the consultation access to the data associated to the set account number.
     * The fields "solde", "nom", "prenom" are suposed available before this call.
     *
     * @throws TraitementException If no account was defined with the specified account number,
     * or to replace errors which come from an SQLException that happen during the process.
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
     * Process the specified traitment operation, in the account, by using "value" and "op" fields, if possible.
     * 
     * @throws TraitementException
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
            System.out.println(e);
            throw new TraitementException("22");
        }
    }

    /**
     *
     * @throws TraitementException
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
            System.out.println(e);
            throw new TraitementException("3");
        }
    }

    /**
     *
     * @return
     */
    public String getDateInf() {
        return dateInf;
    }

    /**
     *
     * @param dateInf
     */
    public void setDateInf(String dateInf) {
        this.dateInf = dateInf;
    }

    /**
     *
     * @return
     */
    public String getDateSup() {
        return dateSup;
    }

    /**
     *
     * @param dateSup
     */
    public void setDateSup(String dateSup) {
        this.dateSup = dateSup;
    }

    /**
     *
     * @return
     */
    public ArrayList<String[]> getOperationsParDates() {
        return operationsParDates;
    }

    /**
     *
     * @param op
     */
    public void setOp(String op) {
        this.op = op;
    }

    /**
     *
     * @param valeur
     */
    public void setValeur(String valeur) {
        this.valeur = new BigDecimal(valeur);
    }

    /**
     *
     * @return
     */
    public String getOp() {
        return op;
    }

    /**
     *
     * @return
     */
    public BigDecimal getAncienSolde() {
        return ancienSolde;
    }

    /**
     *
     * @return
     */
    public BigDecimal getNouveauSolde() {
        return nouveauSolde;
    }

    /**
     *
     * @return
     */
    public String getValeur() {
        return valeur.toString();
    }

    /**
     *
     * @return
     */
    public String getNoCompte() {
        return noCompte;
    }

    /**
     *
     * @return
     */
    public String getNom() {
        return nom;
    }

    /**
     *
     * @return
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * 
     * @return
     */
    public BigDecimal getSolde() {
        return solde;
    }
}
