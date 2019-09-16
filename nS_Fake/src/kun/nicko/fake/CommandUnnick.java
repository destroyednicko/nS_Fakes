package kun.nicko.fake;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandUnnick implements CommandExecutor
{
    private AutoNick pluginInstance;

    public CommandUnnick(AutoNick pluginInstance)
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
                if (PrepareNick.nws(p) == 0)
                {
                    p.playSound(p.getEyeLocation(), Sound.NOTE_BASS, 1.0F, 0.5F);
                } else {
                	pluginInstance.unnick(p);
                }
            } else
            {
                p.sendMessage(AutoNick.CHAT_PREFIX + ChatColor.RED + "Você não tem permissão para isso!");
                p.playSound(p.getEyeLocation(), Sound.NOTE_BASS, 1.0F, 0.5F);
            }
        }
        return true;
    }
}
