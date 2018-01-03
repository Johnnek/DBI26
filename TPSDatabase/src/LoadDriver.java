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
		int old_balance = 0;
		int new_balance = 0;
	
		try {
			ResultSet rs = null;
		
			PreparedStatement getBalance = conn.prepareStatement(
				"select balance " +
				"from tps.branches " +
				"where branchid = ?;"
				);
			PreparedStatement upDateBalance = conn.prepareStatement(
				"update tps.brances " +
				"set balance = ? " +
				"where branchid = ?;"
				);
			rs.close();
			return new_balance;
		} catch (SQLException e) {
			System.err.println(e);
			System.exit(1);
			return 0;
		}
	}

}
