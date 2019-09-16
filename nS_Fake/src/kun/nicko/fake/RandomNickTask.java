package kun.nicko.fake;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import kun.nicko.StormAPI.Reflection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

public class RandomNickTask extends BukkitRunnable {
	private Player p;
	private AutoNick pluginInstance;

	public RandomNickTask(Player p, AutoNick pluginInstance) {
		this.p = p;
		this.pluginInstance = pluginInstance;
	}

	@SuppressWarnings("null")
	@Override
	public void run() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nSFakes", "root", "ASxAYvPKgbw3UmNr");
			statement = connection.createStatement();
			statement.executeQuery("SELECT displayName, profile FROM cached_nicks;");
			resultSet = statement.getResultSet();
			HashMap<String, String> nickList = new HashMap<>();
			while (resultSet.next()) {
				String name = resultSet.getString("displayName");
				if (!pluginInstance.takenNicks.contains(name)) {
					String json = resultSet.getString("profile");
					nickList.put(name, json);
				}
			}
			if (nickList.size() > 0) {
				String[] names = new String[nickList.size()];
				nickList.keySet().toArray(names);
				Random random = new Random();
				String selectedName = names[random.nextInt(names.length)];
				pluginInstance.takenNicks.add(selectedName);
				NickProfile profile = new NickProfile(p, p.getName(), selectedName, getProperties(p, null));
				pluginInstance.nickProfiles.put(p.getUniqueId(), profile);
				String jsonProfile = nickList.get(selectedName);
				JsonParser parser = new JsonParser();
				JsonObject gameProfile = parser.parse(jsonProfile).getAsJsonObject();
				String oldName = p.getName();
				pluginInstance.setNick(p, selectedName, gameProfile);
				NickEvent nickEvent = new NickEvent(p, oldName, selectedName, NickEvent.NickAction.RANDOM_NICK);
				pluginInstance.getServer().getPluginManager().callEvent(nickEvent);
			} else {
				pluginInstance.getServer().getScheduler().callSyncMethod(pluginInstance, () -> {
					for (Player otherP : pluginInstance.getServer().getOnlinePlayers()) {
						if (otherP != p && !otherP.canSee(p)) {
							otherP.showPlayer(p);
						}
					}
					return null;
				});
				p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED
						+ "Nenhum FAKE foi encontrado pra você, desculpe.");
			}
		} catch (Exception e) {
			System.out.println("RandomNickTask!");
			e.printStackTrace();
			p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED
					+ "Erro interno ao processar o comando.");
		} finally {
			try {
				if (resultSet != null && !resultSet.isClosed())
					resultSet.close();
				if (statement != null && !statement.isClosed())
					statement.close();
				if (connection != null && !connection.isClosed())
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public PropertyMap getProperties(Player p, JsonObject jsonProfile) throws Exception {
		PropertyMap map = null;
		try {
			Class<?> clazzCraftPlayer = Reflection.getOBCClass("entity.CraftPlayer");
			if (clazzCraftPlayer.isInstance(p)) {
				Method getHandle = Reflection.getMethod(clazzCraftPlayer, "getHandle");
				Object entityHuman = getHandle.invoke(p);
				Class<?> clazzEntityHuman = Reflection.getNMSClass("EntityHuman");
				if (clazzEntityHuman.isInstance(entityHuman)) {
					// Small hack to circumvent the "final" modifier
					Field modifiersField = Field.class.getDeclaredField("modifiers");
					if (!modifiersField.isAccessible())
						modifiersField.setAccessible(true);

					// Accessing game profile
					GameProfile gameProfile = (GameProfile) Reflection.getMethod(clazzEntityHuman, "getProfile")
							.invoke(entityHuman);
					map = gameProfile.getProperties();
					
					 PropertyMap.Serializer propertiesSerializer = new PropertyMap.Serializer();
		                PropertyMap properties = propertiesSerializer.deserialize(jsonProfile.get("properties"), null, null);
		                Field fieldProperties = Reflection.getField(gameProfile.getClass(), "properties");
		                if (!fieldProperties.isAccessible()) fieldProperties.setAccessible(true);
		                if (Modifier.isFinal(fieldProperties.getModifiers())) modifiersField.setInt(fieldProperties, fieldProperties.getModifiers() & ~Modifier.FINAL);
		                fieldProperties.set(gameProfile, properties);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return map;
	}
}
