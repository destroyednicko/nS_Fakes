package kun.nicko.fake;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UnnickTask extends BukkitRunnable
{
    private Player p;
    private AutoNick pluginInstance;
    public UnnickTask(Player p, AutoNick pluginInstance)
    {
        this.p = p;
        this.pluginInstance = pluginInstance;
    }

    @Override
    public void run()
    {
        try
        {
            NickProfile nickProfile = pluginInstance.getNickProfile(p);
            if (nickProfile != null)
            {
                p.sendMessage(AutoNick.CHAT_PREFIX + "Removendo apelidos falsos...");
                pluginInstance.takenNicks.remove(nickProfile.getDisplayName());
                pluginInstance.nickProfiles.remove(p.getUniqueId());
                String oldName = p.getName();
                pluginInstance.setNick(p, nickProfile.getOriginalName(), nickProfile.getProperties());
                NickEvent nickEvent = new NickEvent(p, oldName, nickProfile.getOriginalName(), NickEvent.NickAction.UNNICK);
                pluginInstance.getServer().getPluginManager().callEvent(nickEvent);
            } else
            {
                p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED + "Perfil do usuário não encontrado, entre em contato com um Admin.");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED + "Erro interno ao processar o comando.");
        }
    }
}
