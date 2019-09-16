package kun.nicko.fake;

import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.entity.Player;

public class NickProfile
{
    private Player p;
    private String originalName;
    private String displayName;
    private final PropertyMap properties;

    public NickProfile(Player p, String originalName, String displayName, PropertyMap properties)
    {
        this.p = p;
        this.originalName = originalName;
        this.displayName = displayName;
        this.properties = properties;
    }

    public Player getPlayer()
    {
        return p;
    }

    public String getOriginalName()
    {
        return originalName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public PropertyMap getProperties()
    {
        return properties;
    }
}