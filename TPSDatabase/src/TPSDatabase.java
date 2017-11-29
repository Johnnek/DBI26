import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


// Neuer Code
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
		if(args.length != 2) {
			System.err.println("usage: java jdbcConnect <user><pwd>\n");
			System.exit(1);
		}else
			try {
			String dbURL = "jdbc:mysql://localhost:3306/tps";
			conn = getConnection(dbURL, args[0], args[1]);
			System.out.println("\nConnected to TPS Database!");

			conn.setAutoCommit(false);

			int n;
			int i;
			int rdm;
			Scanner s = new Scanner(System.in);
			System.out.println("Bitte n eingeben: ");
			n = s.nextInt();
			s.close();

			String branchname = "INGDiBaBankinstitut";
			String baddress = "Musterstrasse 1, 66666 Musterstadt Nord - Rhein - Westfalen, Deutschland";
					
			String accname = "abcdefghijkmnopqrstu";
			String aaddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";
					
			String telname = "abcdeabcdeabcdeabcde";
			String taddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";

			PreparedStatement stmtbranch = conn.prepareStatement(
					"insert into tps.branches values (?,'" + branchname + "',0,'" + baddress + "');"
					);
			PreparedStatement stmtacc = conn.prepareStatement(
					"insert into tps. accounts values (?,'" + accname + "',0,?,'" + aaddress + "');"
					);
			PreparedStatement stmttell = conn.prepareStatement(
					"insert into tps.tellers values(?,'" + telname + "',0,?,'" + taddress + "');"
					);

					for(i = 0; i <= n; i++)
					{
						stmtbranch.setInt(1,  i);
						stmtbranch.executeUpdate();
					}
					for(i = 0; i <= n*100000; i++)
					{
						rdm = (int) (Math.random() * n + 1);
						stmtacc.setInt(1, i);
						stmtacc.setInt(2, rdm);
						stmtacc.executeUpdate();
					}
					for(i = 0; i <= n*10; i++)
					{
						rdm =(int) (Math.random() * n + 1);
						stmttell.setInt(1, i);
						stmttell.setInt(2, rdm);
						stmttell.executeUpdate();
					}

			conn.commit();

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