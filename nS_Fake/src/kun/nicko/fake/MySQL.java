package kun.nicko.fake;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {

	public static String host;
	public static String database;
	public static String user;
	public static String passwd;
	public static Connection con;

	public static void connect() {
		if (!isconnected()) {
			try {
				con = DriverManager.getConnection("jdbc:mysql://localhost:" + "3306/" + "nSFakes" + "?autoReconnect=true", "root", "ASxAYvPKgbw3UmNr");
				System.out.println("[LOBBY] Conectado com sucesso ao SQL.");
			} catch (SQLException e) {
				System.out.println("[LOBBY] Não foi possivel estabelecer uma conexão ao sql.");
			}
		}
	}


	public static void disconnect() {
		if (isconnected()) {
			try {
				con.close();
				System.out.println("[LOBBY] Conexão encerrada.");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean isconnected() {
		return (con == null ? false : true);

	}

	public static PreparedStatement getStatement(String sql) {
		if (isconnected()) {
			PreparedStatement ps;
			try {
				ps = con.prepareStatement(sql);
				return ps;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ResultSet getResult(String sql) {
		if (isconnected()) {
			PreparedStatement ps;
			ResultSet rs;
			try {
				ps = getStatement(sql);
				rs = ps.executeQuery();
				return rs;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}