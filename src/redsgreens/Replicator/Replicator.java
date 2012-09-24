package redsgreens.Replicator;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Replicator extends JavaPlugin implements Listener {

	public ReplicatorConfig Config;
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

/*
    @EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
    {
    	if(event.getAction() == Action.RIGHT_CLICK_AIR)
    	{
    		ItemStack is = ReplicatorUtil.deepCloneItemStack((CraftItemStack)event.getItem());
    		Book book = new CraftBookBuilder().getBook(is);
    		System.out.println(book.getAuthor() + " " + book.getTitle());
    		event.getPlayer().getInventory().setItem(0, book.getItemStack());
    	}
    	
    }
*/
    
}
