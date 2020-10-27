package javaBeans;

import gestionsErreurs.TraitementException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class BOperations {

    private String noCompte, nom, prenom, op, dateInf, dateSup;
    private BigDecimal solde, ancienSolde, nouveauSolde, valeur;
    private ArrayList<String[]> operationsParDates;
    private Connection connect;

    public void setNoCompte(String noCompte) {
        this.noCompte = noCompte;
    }

    public void ouvrirConnexion() {
        try {
            connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/BANQUE?serverTimezone=UTC","root", "admin");
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public void fermerConnexion() {
        try {
            connect.close();
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

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
            throw new TraitementException("22");
        }
    }

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
            System.out.println("err : " + e.getMessage());
            throw new TraitementException("3");
        }
    }

    public String getDateInf() {
        return dateInf;
    }

    public void setDateInf(String dateInf) {
        this.dateInf = dateInf;
    }

    public String getDateSup() {
        return dateSup;
    }

    public void setDateSup(String dateSup) {
        this.dateSup = dateSup;
    }

    public ArrayList<String[]> getOperationsParDates() {
        return operationsParDates;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setValeur(String valeur) {
        this.valeur = new BigDecimal(valeur);
    }

    public String getOp() {
        return op;
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

    public String getNoCompte() {
        return noCompte;
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
