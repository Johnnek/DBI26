import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		
		try {
			ResultSet rs = null;
		
			PreparedStatement getBalanceBranches = conn.prepareStatement(
				"select balance " +
				"from tps.branches " +
				"where branchid = ?;"
				);
			getBalanceBranches.setInt(1, branchid);
			rs = getBalanceBranches.executeQuery();
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			balance += delta;
			PreparedStatement upDateBalanceBranches = conn.prepareStatement(
				"update tps.branches " +
				"set balance = ? " +
				"where branchid = ?;"
				);
			upDateBalanceBranches.setInt(1, balance);
			upDateBalanceBranches.setInt(2, branchid);
			upDateBalanceBranches.executeUpdate();
			
			getBalanceBranches.setInt(1, branchid);
			rs = getBalanceBranches.executeQuery();
			while(rs.next()) {
				balance = rs.getInt(1);
			}
			rs.close();
				return balance;
		} catch (SQLException e) {
			System.err.println(e);
			System.exit(1);
			return 0;
		}
	}

}
