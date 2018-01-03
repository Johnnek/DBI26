import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoadDriver {
/**
 * Funktion, um einen Kontostand von einer accid, die als Eingabeparameter der Funktion �bergeben wird
 * @param accid Account ID von dem die Balance abgefragt werden soll
 */
	public void kontostand_TX(int accid, Connection conn){
		try {
			ResultSet rs = null;
			PreparedStatement getKontostand = conn.prepareStatement(
					"select accid, balance " +
					"from tps.accounts " + 
					"where accid = ?;"
					);
			getKontostand.setInt(1, accid);
			rs = getKontostand.executeQuery();
			while(rs.next()) {
				System.out.println("Accid: " + rs.getInt(1) + "\tBalance: " + rs.getInt(2));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public int einzahlungs_TX(int accid, int tellerid, int branchid, int delta, Connection conn) {
		int balance = 0;
		String cmt = "abcdefghijkmnopqrstuvwxyzabcd";
		try {
			ResultSet rs = null;
		
			Statement getBalance = conn.createStatement();
			Statement upDateBalance = conn.createStatement();
			Statement setHistory = conn.createStatement();

			//balance
			rs = getBalance.executeQuery("select balance " + "from tps.branches " +	"where branchid = '" + branchid + "';");
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			balance += delta;
			upDateBalance.executeUpdate("update tps.branches " +	"set balance = '" + balance + "' " + "where branchid = '" + branchid + "';");
			conn.commit();
			
			//accounts
			rs = getBalance.executeQuery("select balance " + "from tps.accounts " +	"where accid = '" + accid + "';");
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			balance += delta;
			upDateBalance.executeUpdate("update tps.accounts " +	"set balance = '" + balance + "' " + "where accid = '" + accid + "';");
			conn.commit();
			
			//tellers
			rs = getBalance.executeQuery("select balance " + "from tps.tellers " +	"where tellerid = '" + tellerid + "';");
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			balance += delta;
			upDateBalance.executeUpdate("update tps.tellers " +	"set balance = '" + balance + "' " + "where tellerid = '" + tellerid + "';");
			conn.commit();
			
			//ende
			rs = getBalance.executeQuery("select balance " + "from tps.accounts " +	"where accid = '" + accid + "';");
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			
			//history
			setHistory.executeUpdate("insert into tps.history values('" + accid + "', '" + tellerid + "', '" + delta + "', '" + branchid + "', '" + balance + "', '" + cmt + "')");
			conn.commit();
			
			rs.close();
				return balance;
		} catch (SQLException e) {
			System.err.println(e);
			System.exit(1);
			return 0;
		}
	}

}
