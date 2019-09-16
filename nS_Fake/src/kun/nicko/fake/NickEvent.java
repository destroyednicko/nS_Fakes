package kun.nicko.fake;

import org.bukkit.entity.Player;
import org.bukkit.event.*;

public class NickEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private String oldName;
    private String newName;
    private NickAction type;

    public static enum NickAction
    {
        RANDOM_NICK,
        UNNICK
    }

    public NickEvent(Player player, String oldName, String newName, NickAction type)
    {
        this.player = player;
        this.oldName = oldName;
        this.newName = newName;
        this.type = type;
    }

    public Player getPlayer()
    {
        return player;
    }

    public String getOldName()
    {
        return oldName;
    }

    public String getNewName()
    {
        return newName;
    }

    public NickAction getType()
    {
        return type;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
