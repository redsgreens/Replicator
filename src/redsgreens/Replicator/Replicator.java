package redsgreens.Replicator;

import org.bukkit.plugin.java.JavaPlugin;

public class Replicator extends JavaPlugin {

	public ReplicatorConfig Config;
	private ReplicatorListener replicatorListener;
	
    public void onEnable() 
    {
    	Config  = new ReplicatorConfig(this);
    	replicatorListener = new ReplicatorListener(this);
    	
    	// Register the listener
        getServer().getPluginManager().registerEvents(replicatorListener, this);
        
		// print loaded message
        System.out.println(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");

    }
}
