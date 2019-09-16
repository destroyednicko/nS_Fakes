package kun.nicko.fake;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandNickList implements CommandExecutor
{
    private AutoNick pluginInstance;

    public CommandNickList(AutoNick pluginInstance)
    {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args)
    {
        if (sender instanceof Player)
        {
            Player p = (Player) sender;
            if (p.hasPermission("nsfakes.usar"))
            {
                if (pluginInstance.nickProfiles.size() > 0)
                {
                    p.sendMessage(AutoNick.CHAT_PREFIX + "§7Jogador atual:");
                    for (NickProfile profile : pluginInstance.nickProfiles.values())
                    {
                        p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.WHITE + "■ " + ChatColor.AQUA + profile.getOriginalName() +
                                ChatColor.GRAY + " -> " + ChatColor.AQUA + profile.getDisplayName());
                    }
                } else
                {
                    p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED + "Ninguém está com Fake!");
                }
            } else
            {
                p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED + "Você não pode usar este comando!");
                p.playSound(p.getEyeLocation(), Sound.NOTE_BASS, 1.0F, 0.5F);
            }
        }
        return true;
    }
}
