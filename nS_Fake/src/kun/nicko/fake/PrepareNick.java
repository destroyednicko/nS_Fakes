package kun.nicko.fake;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PrepareNick {

	public static boolean isregi(Player p) {
		String uuid = p.getUniqueId().toString();
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, uuid);
			ResultSet rs = ps.executeQuery();
			boolean user = rs.next();
			rs.close();
			rs.close();
			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	public static boolean isregi(UUID uuid) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			boolean user = rs.next();
			rs.close();
			rs.close();
			return user;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static int nws(Player p) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			int ach = rs.getInt("ShouldNick");
			rs.close();
			ps.close();
			return ach;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}
	public static int nws(UUID uuid) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			int ach = rs.getInt("ShouldNick");
			rs.close();
			ps.close();
			return ach;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static String getrealUUID(Player p) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE PlayerName= ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			String ach = rs.getString("UUID");
			rs.close();
			ps.close();
			return ach;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getname(Player p) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, p.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			String ach = rs.getString("NickName");
			rs.close();
			ps.close();
			return ach;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getname(UUID uuid) {
		try {
			PreparedStatement ps = MySQL.getStatement("SELECT * FROM Nick WHERE UUID= ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			String ach = rs.getString("NickName");
			rs.close();
			ps.close();
			return ach;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
