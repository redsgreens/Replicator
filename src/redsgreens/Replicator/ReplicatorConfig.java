package redsgreens.Replicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ReplicatorConfig {

	private Replicator Plugin;	
	private HashMap<String,Object> ConfigMap = new HashMap<String, Object>(); 

	public Boolean ShowErrorsInClient = false;
	public Boolean AllowNonOpAccess = false;
	public Boolean VerboseStartup = false;
	
	private PermissionHandler Permissions = null;
	

	public ReplicatorConfig(Replicator plugin)
	{
		Plugin = plugin;
		
		setupPermissions();
		
		try
		{
			loadConfig();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// load config settings from config.yml
	@SuppressWarnings("unchecked")
	public void loadConfig() throws Exception {
		
		// create the data folder if it doesn't exist
		File folder = Plugin.getDataFolder();
    	if(!folder.exists()){
    		folder.mkdirs();
    	}
		
		// create the file from the one in the jar if it doesn't exist on disk
    	File configFile = new File(folder, "config.yml");
		if (!configFile.exists()){
			configFile.createNewFile();
			InputStream res = Replicator.class.getResourceAsStream("/config.yml");
			FileWriter tx = new FileWriter(configFile);
			for (int i = 0; (i = res.read()) > 0;) tx.write(i);
			tx.flush();
			tx.close();
			res.close();
		}
		
		BufferedReader rx = new BufferedReader(new FileReader(configFile));
		Yaml yaml = new Yaml();
		
		ConfigMap.clear();
		
		try{
			ConfigMap = (HashMap<String,Object>)yaml.load(rx);
		}
		finally
		{
			rx.close();
		}
		
		if(ConfigMap.containsKey("ShowErrorsInClient")){
			boolean configBool = (Boolean)ConfigMap.get("ShowErrorsInClient");
			if(configBool)
				ShowErrorsInClient = true;
		}

		if(ConfigMap.containsKey("VerboseStartup")){
			boolean configBool = (Boolean)ConfigMap.get("VerboseStartup");
			if(configBool)
				VerboseStartup = true;
		}

		if(ConfigMap.containsKey("AllowNonOpAccess")){
			boolean configBool = (Boolean)ConfigMap.get("AllowNonOpAccess");
			if(configBool)
				AllowNonOpAccess = true;
		}

		if(VerboseStartup)
		{
			Plugin.getLogger().log(Level.INFO, "ShowErrorsInClient=" + ShowErrorsInClient);
			Plugin.getLogger().log(Level.INFO, "AllowNonOpAccess=" + AllowNonOpAccess);
		}
		
	}

	// return true if Player p has the permission perm
    public boolean isAuthorized(Player p, String perm){
    	boolean retval = p.isOp();

    	if(Permissions == null && retval == false)
    	{

    		if(AllowNonOpAccess == true && perm.equalsIgnoreCase("access"))
    			return true;

    		try
    		{
    			return p.hasPermission("replicator." + perm);
    		}
    		catch (Exception ex){}
    	}
    	else
    	{
        	try{
        		if(Permissions != null)
        			  if (Permissions.has(p, "replicator." + perm))
        			      retval = true;
        	}
        	catch (Exception ex){}
    	}

    	return retval;	
    }
    
    public boolean isAuthorized(String playerName, String perm)
    {
    	return isAuthorized(Plugin.getServer().getPlayer(playerName), perm);
    }
    
    public void setupPermissions() {
    	try{
            Plugin test = Plugin.getServer().getPluginManager().getPlugin("Permissions");

            if (Permissions == null) {
                if (test != null) {
                    Permissions = ((Permissions)test).getHandler();
                    if(VerboseStartup)
                    	Plugin.getLogger().log(Level.INFO, "Found permissions handler " + test.getDescription().getName() + " " + test.getDescription().getVersion());
                }
            }
    	}
    	catch (Exception ex){}
    }
}
