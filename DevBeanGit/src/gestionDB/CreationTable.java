package gestionDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreationTable {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connect = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/BANQUE?serverTimezone=UTC",
				"root",
				"Jp22102810"
				);
		Statement statmt = connect.createStatement();
		statmt.execute("CREATE TABLE COMPTE"
				+ "("
				+ "nocompte VARCHAR(4) NOT NULL,"
				+ "nom VARCHAR(20),"
				+ "prenom VARCHAR(20),"
				+ "solde DECIMAL(10, 2) NOT NULL, "
				+ "PRIMARY KEY (nocompte)"
				+ ");");
		statmt.execute("CREATE TABLE OPERATION"
				+ "("
				+ "nocompte CHAR(4) NOT NULL,"
				+ "date DATE NOT NULL,"
				+ "heure TIME NOT NULL,"
				+ "op CHAR(1) NOT NULL,"
				+ "valeur DECIMAL(10,2) NOT NULL"
				+ ");");
		statmt.close();
		connect.close();
	}
	
	/*
	CREATE TABLE cats
	(
	  id              INT unsigned NOT NULL AUTO_INCREMENT, # Unique ID for the record
	  name            VARCHAR(150) NOT NULL,                # Name of the cat
	  owner           VARCHAR(150) NOT NULL,                # Owner of the cat
	  birth           DATE NOT NULL,                        # Birthday of the cat
	  PRIMARY KEY     (id)                                  # Make the id the primary key
	);
	*/
}
