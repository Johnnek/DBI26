//import java.sql.*;
//import java.util.InputMismatchException;
//import java.util.Scanner;
//
//
//// Alter Code
//public class TPSConnect {
//
//	protected static Scanner s = new Scanner(System.in);
//	
//	public static Connection getConnection(String dbUrl, String user, String pw)
//	throws SQLException
//	{
//		try
//		{
//			return DriverManager.getConnection(dbUrl, user, pw);
//		}
//		catch (SQLException e)
//		{
//			throw new SQLException("JDBC driver not found!");
//		}
//	} // end getConnection
//	
//	protected static String getInput(String prompt)
//	{
//		try
//		{
//			System.out.print(prompt);
//			return s.nextLine();
//		}
//		catch(InputMismatchException e)
//		{
//			System.err.println(e);
//			return null;
//		}
//	} // end getInput
//
//	public static void main(String[] args) {
//
//		{
//			if(args.length!=2) 
//			{
//				System.err.println("\nusage: java jdbcConnect <userid> <passwd>\n");
//				System.exit(1);
//			}
//			else
//				try
//			{
//					String dbUrl = "jdbc:mysql://localhost:3306/tps"; // local
//					// String user = "root";
//					// String pw = "dbi26";
//					Connection conn = getConnection(dbUrl, args[0], args[1]);
//					System.out.println("\nConnected to TPS database!\n");
//				
//					
//					conn.setAutoCommit(false);
//					int i;
//					int n;
//					int rdm;
//					System.out.println("Bitte Anzahl eingeben: ");
//					n = s.nextInt();
//					s.close();
//					
//					String branchname = "INGDiBaBankinstitut";
//					String baddress = "Musterstrasse 1, 66666 Musterstadt Nord - Rhein - Westfalen, Deutschland";
//					
//					String accname = "abcdefghijkmnopqrstu";
//					String aaddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";
//					
//					String telname = "abcdeabcdeabcdeabcde";
//					String taddress = "Musterstrasse 1, 66666 Musterstadt Nord-Rhein-Westfalen, Deutschland";
//					
//					String cmmnt = "INGDiBaBankinstitut0123456789";
//					
//					System.out.println(cmmnt);
//					
//					int balance = 0;
//					System.out.println(accname.length());
//					
//					PreparedStatement stmtbranch = conn.prepareStatement("insert into tps.branches values(' ? ','" + branchname + "','" + balance + "','" + baddress + "');");
//
//					PreparedStatement stmtacc = conn.prepareStatement("insert into tps.accounts values(' ? ','" + accname + "','" + balance + "',' ? ','" + aaddress + "');");
//
//					PreparedStatement stmttell = conn.prepareStatement("insert into tps.tellers values(' ? ','" + telname + "','" + balance + "',' ? ','" + taddress + "');");
//
//					for(i = 0; i <= n; i++)
//					{
//						stmtbranch.setInt(1,  i);
//						stmtbranch.executeUpdate();
//					}
//					for(i = 0; i <= n*100000; i++)
//					{
//						rdm = (int) (Math.random() * n + 1);
//						stmtacc.setInt(1, i);
//						stmtacc.setInt(2, rdm);
//						stmtacc.executeUpdate();
//					}
//					for(i = 0; i <= n*10; i++)
//					{
//						rdm =(int) (Math.random() * n + 1);
//						stmttell.setInt(1, i);
//						stmttell.setInt(2, rdm);
//						stmttell.executeUpdate();
//					}
//					
//			           
//			            conn.close();
//			            System.out.println("Disconnected!");
//			           
//			         }
//			         catch (SQLException e)
//			         {
//			            System.err.println(e);
//			            System.exit(1);
//			         }
//			   }  // end main
//			}  // end class CustomerOverview
//}