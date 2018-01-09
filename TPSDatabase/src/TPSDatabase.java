import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * 
 * @author Marius Preikschat und John Parol
 * 
 *
 */

public class TPSDatabase {

protected static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	//Connection Start
	protected static Connection getConnection(String dbURL, String user, String pwd) 
			throws SQLException{
		try {
			return DriverManager.getConnection(dbURL, user, pwd);
		}
		catch(SQLException e){
			throw new SQLException("JDBC Driver not found!"); 
		}
	}
	//Connection finished

	//Input start
	protected static String getInput(String prompt) {
		try {
			System.out.println(prompt);
			return stdin.readLine();
		}
		catch(IOException ex) {
			System.err.println(ex);
			return null;
		}
	}
	//Input finished
public static Connection conn;

public static void createDatabase() throws SQLException {
	ResultSet resultSet = conn.getMetaData().getCatalogs();

	/**
	 * Überprüft im DBMS, ob die Benchmark Datenbank TPS bereits existiert, wenn ja, dann wird diese gelöscht
	 */
        while (resultSet.next()) {

          String databaseName = resultSet.getString(1);
            if(databaseName.equals("tps")){
            	PreparedStatement dropDatabase = conn.prepareStatement(
						"drop database tps;"
						);
            	dropDatabase.executeUpdate();
            	conn.commit();
            	System.out.println("Existing Database TPS was dropped!");
            }
        }
        resultSet.close();

        /**
         * Benchmark Datenbank TPS wird erstellt
         */
		PreparedStatement createDatabase = conn.prepareStatement(
				"create database tps; "
				);
		PreparedStatement useDatabase = conn.prepareStatement(
				"use tps;"
				);
		createDatabase.executeUpdate();
		useDatabase.executeUpdate();
		conn.commit();
		
		System.out.println("Database TPS created!");
}

public static void createTables() throws SQLException {
	/**
	 * Benchmark Datenbank TPS wird mit den benötigten Tabellen gefüllt
	 */
	Statement use = conn.createStatement();
	use.executeUpdate("use tps;");
	conn.commit();
	
	Statement createBranches = conn.createStatement();
	createBranches.executeUpdate(
			"create table branches " + 
			"( branchid int not null," + 
			" branchname char(20) not null," + 
			" balance int not null," + 
			" address char(72) not null," + 
			" primary key (branchid) );"
			);	
	conn.commit();
	
	Statement createAccounts = conn.createStatement();
	createAccounts.executeUpdate(
			"create table accounts " + 
			"( accid int not null," + 
			" name char(20) not null," + 
			" balance int not null," + 
			" branchid int not null," + 
			" address char(68) not null," + 
			" primary key (accid)," + 
			" foreign key (branchid) references branches (branchid) );"
			);
	conn.commit();
	
	Statement createTellers = conn.createStatement();
	createTellers.executeUpdate(
			"create table tellers " + 
			"( tellerid int not null," + 
			" tellername char(20) not null," + 
			" balance int not null," + 
			" branchid int not null," + 
			" address char(68) not null," + 
			" primary key (tellerid)," + 
			" foreign key (branchid) references branches (branchid) );"
			);
	conn.commit();
	
	Statement createHistory = conn.createStatement();
	createHistory.executeUpdate(
			"create table history " + 
			"( accid int not null," + 
			" tellerid int not null," + 
			" delta int not null," + 
			" branchid int not null," + 
			" accbalance int not null," + 
			" cmmnt char(30) not null," + 
			" foreign key (accid) references accounts (accid)," + 
			" foreign key (tellerid) references tellers (tellerid)," + 
			" foreign key (branchid) references branches (branchid) );"
			);
	conn.commit();
}

public static int getEingabeN() {
	/**
	 * Eingabe des Parameters n, um den Benchmark durchzuführen
	 */
	int n;
	Scanner s = new Scanner(System.in);
	System.out.println("Bitte n eingeben: ");
	n = s.nextInt();
	s.close();
	return n;
}

	public static void main(String[] args) {
		
		Timer t = new Timer();//Timer zur Zeiterfassung für den Benchmark
		
		if(args.length != 2) {
			System.err.println("usage: java jdbcConnect <user><pwd>\n");
			System.exit(1);
		}else
			try {
			String dbURL = "jdbc:mysql://localhost:3306?verifyServerCertificate=false&useSSL=true&useServerPrepStmts=false&rewriteBatchedStatements=true";
			conn = getConnection(dbURL, args[0], args[1]);
			System.out.println("Connected to DBMS!");

			/**
			 * 1. Optimierung, aus nicht jede Änderungen an das DBMS gepusht werden, sondern am Ende ein Commit reicht, um die Änderungen zu pushen 
			 */
			conn.setAutoCommit(false);
			
			createDatabase();
			createTables();
			
			int n = getEingabeN();

			String name = "INGDiBaBankinsititut";
			String branchAddress = "Musterstrasse 1, 66666 Musterstadt Nord - Rhein - Westfalen, Deutschland";
			String accountsAddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";
			int balance = 0;
			
			LoadDriver ld = new LoadDriver();
			
			/**
			 * 2. Optimierung des Benchmarks, in dem Prepared Statements benutzt werden
			 */			
			PreparedStatement stmt_branches = conn.prepareStatement(
					"insert into tps.branches values (?, ?, ?, ?)"
					);
			PreparedStatement stmt_accounts = conn.prepareStatement(
					"insert into tps.accounts values (?, ?, ?, ?, ?)"
					);
			PreparedStatement stmt_tellers = conn.prepareStatement(
					"insert into tps.tellers values (?, ?, ?, ?, ?)"
					);
					
			Statement foreignKeyCheckOFF = conn.createStatement();					
			Statement foreignKeyCheckON = conn.createStatement();
			Statement uniqueCheckON = conn.createStatement();
			Statement uniqueCheckOFF = conn.createStatement();

			int zufall_BranchID;
			int i;
			int count = 0;
			final int batchSize = 10000;
			
			Statement br_leeren = conn.createStatement();
			br_leeren.executeUpdate(
					"optimize table tps.branches"
					);
			
			Statement ac_leeren = conn.createStatement();
			ac_leeren.executeUpdate(
					"optimize table tps.accounts"
					);
			
			Statement ts_leeren = conn.createStatement();
			ts_leeren.executeUpdate(
					"optimize table tps.tellers"
					);
			
			Statement hs_leeren = conn.createStatement();
			hs_leeren.executeUpdate(
					"optimize table tps.history"
					);
			conn.commit();
			
			/**
			 * Start des Timers
			 */
			t.start();
			
			foreignKeyCheckON.executeUpdate(
					"set foreign_key_checks = 1"
					);
			uniqueCheckON.executeUpdate(
					"set unique_checks = 0"
					);
			
			for(i = 1; i <= n; i++) {
				stmt_branches.setInt(1, i);
				stmt_branches.setString(2, name);
				stmt_branches.setInt(3, balance);
				stmt_branches.setString(4, branchAddress);
				stmt_branches.addBatch();
				
				if(++count % batchSize == 0) {
					stmt_branches.executeBatch();
				}
			}
			stmt_branches.executeBatch();	
			
			count = 0;
			for(i = 1; i <= n*100000; i++) {
				zufall_BranchID = (int)Math.random() * n + 1;
				
				stmt_accounts.setInt(1, i);
				stmt_accounts.setString(2, name);
				stmt_accounts.setInt(3, balance);
				stmt_accounts.setInt(4, zufall_BranchID);
				stmt_accounts.setString(5, accountsAddress);
				stmt_accounts.addBatch();
				
				if(++count % batchSize == 0) {
					stmt_accounts.executeBatch();
				}
			}
			stmt_accounts.executeBatch();
			
			count = 0;
			for(i = 1; i <= n*10; i++) {
				zufall_BranchID = (int)Math.random() * n + 1;
				
				stmt_tellers.setInt(1, i);
				stmt_tellers.setString(2, name);
				stmt_tellers.setInt(3, balance);
				stmt_tellers.setInt(4, zufall_BranchID);
				stmt_tellers.setString(5, accountsAddress);
				stmt_tellers.addBatch();
				
				if(++count % batchSize == 0) {
					stmt_tellers.executeBatch();
				}
			}
			stmt_tellers.executeBatch();
			
			foreignKeyCheckOFF.executeUpdate(
					"set foreign_key_checks = 0"
					); 
			uniqueCheckOFF.executeUpdate(
					"set unique_checks = 1"
					);
			
			conn.commit();
			/**
			 * Stop des Timers
			 */
			double te = t.stop();
			
			System.out.println(te + " Sekunden");

			ld.kontostand_TX(19, conn);
			System.out.println(ld.einzahlungs_TX(19, 5, 1, 1337, conn));
			
			for(int k = 0; k < 10; k++) {
				ld.einzahlungs_TX(k+1, k+1, k, 19, conn);
			}
			
			System.out.println(ld.analyse_tx(19, conn));
			
//			int nb = einzahlungs_TX(1,2,3,4);
//			System.out.println("\n\n" + nb);
			
			stmt_branches.close();
			stmt_accounts.close();
			stmt_tellers.close();
			conn.close();
			
		    System.out.println("Disconnected!");

		         }
		         catch (SQLException e)
		         {
		            System.err.println(e);
		            System.exit(1);
		         }
	}

}
