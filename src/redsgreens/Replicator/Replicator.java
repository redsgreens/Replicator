package redsgreens.Replicator;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Replicator extends JavaPlugin implements Listener {

	public static ReplicatorConfig Config;
	private ReplicatorListener replicatorListener;
	
    public void onEnable() 
    {
    	Config  = new ReplicatorConfig(this);
    	replicatorListener = new ReplicatorListener(this);
    	
    	// Register the listener
        getServer().getPluginManager().registerEvents(replicatorListener, this);
        
        // for testing only
        //getServer().getPluginManager().registerEvents(this, this);

    }
    
    public void onDisable()
    {
    	Config = null;
    	replicatorListener = null;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
    {
    	if(event.getAction() == Action.RIGHT_CLICK_AIR)
    	{
    		CraftItemStack is = (new SerializableItemStack((CraftItemStack)event.getItem())).getItemStack();
    		event.getPlayer().getInventory().setItem(0, is);
    	}
    	
    }

    
}
