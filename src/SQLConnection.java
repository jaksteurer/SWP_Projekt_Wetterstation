import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {

	public static void main(String[] args) {
		
	}
	 public Connection getConnection() throws ClassNotFoundException, SQLException {
		try {
			Connection con = null;
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SWP_Weatherstation"
					+"?serverTimezone=UTC&useSSL=false",	// DB
					"root",                                	// User
					"hallo123"                         		// Passwort
					);
			System.out.println("Verbindung erfolgreich");
			return con;
		}catch(Exception e) {
			System.out.println("Verbindung Fehlgeschlagen! Werte werden nicht in der Datenbank gespeichert!"
					+ "\nKontrollieren ob MySQL Workbench geöffnet ist.");
			System.out.println("[getConnection]Fehler: "+e+"\n");
		}
		return null;
	}
	public void releaseConnection (Connection con) 
			throws SQLException {
		if (con != null)
			con.close();
		System.out.println("Verbindung erfolgreich getrennt");
	}	
}
