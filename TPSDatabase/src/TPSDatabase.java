import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * 
 * @author Marius Preikschat und John Parol
 * 
 * swtweb.w-hs.de/bobepo
 * dbi26-dbdfgsi26
 *
 */

/**
 * Klasse Timer zur Zeiterfassung des Benchmarks
 *
 */
class Timer{
	/**
	 * Variable, die vom System die Zeit in Millisekunden speichert
	 */
	long millis;
	
	/**
	 * Funktion zum Starten der Zeiterfassung
	 */
	void start() {
		millis = System.currentTimeMillis();
	}
	
	/**
	 * Funktion zum Stoppen der Zeiterfassung
	 */
	void stop() {
		millis = System.currentTimeMillis() - millis;
		double dmillis = (double)millis/1000;
		System.out.println(dmillis + " Sekunden");
	}
}

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
static Connection conn;

	public static void main(String[] args) {
		
		Timer t = new Timer();//Timer zur Zeiterfassung für den Benchmark
		
		if(args.length != 2) {
			System.err.println("usage: java jdbcConnect <user><pwd>\n");
			System.exit(1);
		}else
			try {
			String dbURL = "jdbc:mysql://localhost:3306?verifyServerCertificate=false&useSSL=true";
			conn = getConnection(dbURL, args[0], args[1]);
			System.out.println("Connected to DBMS!");

			/**
			 * 1. Optimierung, aus nicht jede Änderungen an das DBMS gepusht werden, sondern am Ende ein Commit reicht, um die Änderungen zu pushen 
			 */
			conn.setAutoCommit(false);
			
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
				
				/**
				 * Benchmark Datenbank TPS wird mit den benötigten Tabellen gefüllt
				 */
				PreparedStatement createBranches = conn.prepareStatement(
						"create table branches " + 
						"( branchid int not null," + 
						" branchname char(20) not null," + 
						" balance int not null," + 
						" address char(72) not null," + 
						" primary key (branchid) );"
						);		
				createBranches.executeUpdate();
				conn.commit();
				
				PreparedStatement createAccounts = conn.prepareStatement(
						"create table accounts " + 
						"( accid int not null," + 
						" name char(20) not null," + 
						" balance int not null," + 
						" branchid int not null," + 
						" address char(68) not null," + 
						" primary key (accid)," + 
						" foreign key (branchid) references branches (branchid) );"
						);
				createAccounts.executeUpdate();
				conn.commit();
				
				PreparedStatement createTellers = conn.prepareStatement(
						"create table tellers " + 
						"( tellerid int not null," + 
						" tellername char(20) not null," + 
						" balance int not null," + 
						" branchid int not null," + 
						" address char(68) not null," + 
						" primary key (tellerid)," + 
						" foreign key (branchid) references branches (branchid) );"
						);
				createTellers.executeUpdate();
				conn.commit();
				
				PreparedStatement createHistory = conn.prepareStatement(
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
				createHistory.executeUpdate();
				conn.commit();

			/**
			 * Eingabe des Parameters n, um den Benchmark durchzuführen
			 */
			int n;
			Scanner s = new Scanner(System.in);
			System.out.println("Bitte n eingeben: ");
			n = s.nextInt();
			s.close();

			String name = "INGDiBaBankinsititut";
			String branchAddress = "Musterstrasse 1, 66666 Musterstadt Nord - Rhein - Westfalen, Deutschland";
			String accountsAddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";

			/**
			 * 2. Optimierung des Benchmarks, in dem Prepared Statements benutzt werden
			 */
			PreparedStatement stmt_branches = conn.prepareStatement(
					"insert into tps.branches values (?,'" + name + "',0,'" + branchAddress + "');"
					);
			PreparedStatement stmt_accounts = conn.prepareStatement(
					"insert into tps. accounts values (?,'" + name + "',0,?,'" + accountsAddress + "');"
					);
			PreparedStatement stmt_tellers = conn.prepareStatement(
					"insert into tps.tellers values(?,'" + name + "',0,?,'" + accountsAddress + "');"
					);

			int zufall_BranchID;
			/**
			 * Start des Timers
			 */
			t.start();
			
			for(int i = 1; i <= n; i++) {
				stmt_branches.setInt(1, i);
				stmt_branches.executeUpdate();
			}
			for(int i = 1; i <= n*100000; i++) {
				zufall_BranchID = (int)Math.random() * n + 1;
				stmt_accounts.setInt(1, i);
				stmt_accounts.setInt(2, zufall_BranchID);
				stmt_accounts.executeUpdate();
			}
			for(int i = 1; i <= n*10; i++) {
				zufall_BranchID = (int)Math.random() * n + 1;
				stmt_tellers.setInt(1, i);
				stmt_tellers.setInt(2, zufall_BranchID);
				stmt_tellers.executeUpdate();
			}
			
			
			
			conn.commit();
			
			/**
			 * Stop des Timers
			 */
			t.stop();
			
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
