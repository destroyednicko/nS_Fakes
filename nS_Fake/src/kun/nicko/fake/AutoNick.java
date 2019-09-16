package kun.nicko.fake;

import com.google.gson.JsonObject;
import com.mojang.authlib.properties.PropertyMap;

import kun.nicko.StormAPI.Main;
import kun.nicko.StormAPI.Reflection;
import kun.nicko.StormAPI.Registry;
import kun.nicko.StormAPI.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.*;
import java.util.*;

public class AutoNick extends JavaPlugin implements Listener
{
	public static final String CHAT_PREFIX = "§8┃ §5nSFakes §8┃ §7";
    protected ArrayList<String> takenNicks = new ArrayList<>();
    protected HashMap<UUID, NickProfile> nickProfiles = new HashMap<>();
    private boolean isEnabled = true;
    private Main util;

    @Override
    public void onEnable()
    {
    	
    	MySQL.connect();
        getLogger().info("oOo nsFakes - Ativo...");
        util = Util.getUtil();
       
    
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("unnick").setExecutor(new CommandUnnick(this));
        getCommand("nicklist").setExecutor(new CommandNickList(this));
    }

    public void setNick(Player p, String displayName, JsonObject jsonProfile) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException
    {
        Class clazzCraftPlayer = Reflection.getOBCClass("entity.CraftPlayer");
        if (clazzCraftPlayer.isInstance(p))
        {
            Method getHandle = Reflection.getMethod(clazzCraftPlayer, "getHandle");
            Object entityHuman = getHandle.invoke(p);
            Class clazzEntityHuman = Reflection.getNMSClass("EntityHuman");
            if (clazzEntityHuman.isInstance(entityHuman))
            {
                // Small hack to circumvent the "final" modifier
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                if (!modifiersField.isAccessible()) modifiersField.setAccessible(true);

                // Accessing game profile
                Object gameProfile = Reflection.getMethod(clazzEntityHuman, "getProfile").invoke(entityHuman);

                Field fieldName = Reflection.getField(gameProfile.getClass(), "name");
                if (!fieldName.isAccessible()) fieldName.setAccessible(true);
                if (Modifier.isFinal(fieldName.getModifiers())) modifiersField.setInt(fieldName, fieldName.getModifiers() & ~Modifier.FINAL);
                fieldName.set(gameProfile, displayName);

                PropertyMap.Serializer propertiesSerializer = new PropertyMap.Serializer();
                PropertyMap properties = propertiesSerializer.deserialize(jsonProfile.get("properties"), null, null);
                Field fieldProperties = Reflection.getField(gameProfile.getClass(), "properties");
                if (!fieldProperties.isAccessible()) fieldProperties.setAccessible(true);
                if (Modifier.isFinal(fieldProperties.getModifiers())) modifiersField.setInt(fieldProperties, fieldProperties.getModifiers() & ~Modifier.FINAL);
                fieldProperties.set(gameProfile, properties);
                p.setDisplayName(displayName);
                getServer().getScheduler().callSyncMethod(this, () -> {
                    for (Player otherP : getServer().getOnlinePlayers())
                    {
                        if (otherP != p /*&& !otherP.hasPermission("nsfakes.ignorenicks")*/) // TODO: Proper ignore-nick feature
                        {
                            otherP.hidePlayer(p);
                            otherP.showPlayer(p);
                        }
                    }
                    return null;
                });

                p.sendMessage(CHAT_PREFIX + "§7Agora você está nomeado como: §e" + displayName);
            }
        }
    }

    public void setNick(Player p, String displayName, PropertyMap properties) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException
    {
        Class clazzCraftPlayer = Reflection.getOBCClass("entity.CraftPlayer");
        if (clazzCraftPlayer.isInstance(p))
        {
            Method getHandle = Reflection.getMethod(clazzCraftPlayer, "getHandle");
            Object entityHuman = getHandle.invoke(p);
            Class clazzEntityHuman = Reflection.getNMSClass("EntityHuman");
            if (clazzEntityHuman.isInstance(entityHuman))
            {
                // Small hack to circumvent the "final" modifier
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                if (!modifiersField.isAccessible()) modifiersField.setAccessible(true);

                // Accessing game profile
                Object gameProfile = Reflection.getMethod(clazzEntityHuman, "getProfile").invoke(entityHuman);

                Field fieldName = Reflection.getField(gameProfile.getClass(), "name");
                if (!fieldName.isAccessible()) fieldName.setAccessible(true);
                if (Modifier.isFinal(fieldName.getModifiers())) modifiersField.setInt(fieldName, fieldName.getModifiers() & ~Modifier.FINAL);
                fieldName.set(gameProfile, displayName);

                Field fieldProperties = Reflection.getField(gameProfile.getClass(), "properties");
                if (!fieldProperties.isAccessible()) fieldProperties.setAccessible(true);
                if (Modifier.isFinal(fieldProperties.getModifiers())) modifiersField.setInt(fieldProperties, fieldProperties.getModifiers() & ~Modifier.FINAL);
                fieldProperties.set(gameProfile, properties);
                p.setDisplayName(displayName);
                getServer().getScheduler().callSyncMethod(this, () -> {
                    for (Player otherP : getServer().getOnlinePlayers())
                    {
                        if (otherP != p /*&& !otherP.hasPermission("nsfakes.ignorenicks")*/) // TODO: Proper ignore-nick feature
                        {
                            otherP.hidePlayer(p);
                            otherP.showPlayer(p);
                        }
                    }
                    return null;
                });

                p.sendMessage(CHAT_PREFIX + "§7Agora você está nomeado como: §e" + displayName);
            }
        }
    }

    public void setNicksEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }

    public boolean isNicksEnabled()
    {
        return isEnabled;
    }

    public boolean isPlayerNicked(Player p)
    {
        return nickProfiles.containsKey(p.getUniqueId());
    }

    public Player getNickedPlayerFromOriginalName(String name)
    {
        for (NickProfile profile : nickProfiles.values())
        {
            if (profile.getOriginalName().equals(name))
            {
                return profile.getPlayer();
            }
        }
        return null;
    }

    public Player getNickedPlayerFromNick(String name)
    {
        for (NickProfile profile : nickProfiles.values())
        {
            if (profile.getDisplayName().equals(name))
            {
                return profile.getPlayer();
            }
        }
        return null;
    }

    public NickProfile getNickProfile(Player p)
    {
        if (isPlayerNicked(p))
        {
            return nickProfiles.get(p.getUniqueId());
        } else
        {
            return null;
        }
    }

    public boolean unnick(Player p)
    {
        if (isPlayerNicked(p) && nickProfiles.containsKey(p.getUniqueId()))
        {
            new UnnickTask(p, this).runTaskAsynchronously(this);
            return true;
        }
        return false;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e)
    {
        if (isEnabled && e.getPlayer().hasPermission("nsfakes.usar"))
        {
          if (PrepareNick.nws(e.getPlayer()) == 1) {
            {
                for (Player p : getServer().getOnlinePlayers())
                {
                    if (p != e.getPlayer())
                    {
                        p.hidePlayer(e.getPlayer());
                    }
                }
            }
          }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        if (e.getPlayer().hasPermission("nsfakes.usar"))
        {
            if (isEnabled)
            {
               if (PrepareNick.nws(e.getPlayer()) == 1) {
                    Player p = e.getPlayer();
                    new RandomNickTask(p, this).runTaskLater(this, 1L);
                }
            }
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    if (nickProfiles.size() > 0)
                    {
                        e.getPlayer().sendMessage(CHAT_PREFIX + "§7Existem §e" + nickProfiles.size() + " §7usuários com Fake aqui.");
                        e.getPlayer().sendMessage(CHAT_PREFIX + "§7Para ver quem são, digite: §e/nicklist");
                    }
                }
            }.runTaskLater(this, 1L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        if (isPlayerNicked(e.getPlayer()))
        {
            if (takenNicks.contains(e.getPlayer().getDisplayName()))
            {
                takenNicks.remove(e.getPlayer().getDisplayName());
            }
            nickProfiles.remove(e.getPlayer().getUniqueId());
        }
    }

    static
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}