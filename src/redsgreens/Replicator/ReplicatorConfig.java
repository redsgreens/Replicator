package redsgreens.Replicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class ReplicatorConfig {

	private Replicator Plugin;	
	private HashMap<String,Object> ConfigMap = new HashMap<String, Object>(); 

	public Boolean ShowErrorsInClient = false;
	public Boolean AllowNonOpAccess = false;
	public Boolean VerboseStartup = false;
	public String SignTag = "Replicator";
	
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
    	if(!folder.exists())
    		folder.mkdirs();
		
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

		if(ConfigMap.containsKey("SignTag")){
			SignTag = (String)ConfigMap.get("SignTag");
		}

		if(VerboseStartup)
		{
			Plugin.getLogger().log(Level.INFO, "ShowErrorsInClient=" + ShowErrorsInClient);
			Plugin.getLogger().log(Level.INFO, "AllowNonOpAccess=" + AllowNonOpAccess);
			Plugin.getLogger().log(Level.INFO, "SignTag=" + SignTag);
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
    
    @SuppressWarnings("unchecked")
	public ItemStack[] loadInventory(Location loc)
    {
    	File folder = new File(Plugin.getDataFolder(), "data");
    	if(!folder.exists())
    	{
    		folder.mkdirs();
    		return null;
    	}

    	String fileName = loc.getWorld().getName() + "_" + Math.round(loc.getX()) + "_" + Math.round(loc.getY()) + "_" + Math.round(loc.getZ()) + ".yml";
    	File file = new File(folder, fileName);
    	
    	if(!file.exists())
    		return null;
    	
    	FileInputStream fis = null;
    	Yaml yaml = new Yaml(new CustomClassLoaderConstructor(getClass().getClassLoader()));
    	ArrayList<SerializableItemStack> sItems = null;

		try 
		{
			fis = new FileInputStream(file);
			sItems = (ArrayList<SerializableItemStack>)yaml.load(fis);
			fis.close();
		} 
		catch (Exception e) 
		{
			if(fis != null)
			{
				try {
					fis.close();
				} catch (IOException e1) {
					Plugin.getLogger().log(Level.WARNING, e.getMessage());
				}
			}
			Plugin.getLogger().log(Level.WARNING, e.getMessage());
		}
    
		if(sItems != null)
		{
			CraftItemStack[] retval = new CraftItemStack[sItems.size()];
			for(int i=0; i<sItems.size(); i++)
			{
				if(sItems.get(i) == null)
					retval[i] = null;
				else
					retval[i] = sItems.get(i).getItemStack();
			}
			return retval;
		}
		
    	return null;
    }
    
    public void saveInventory(ItemStack[] items, Location loc)
    {
    	File folder = new File(Plugin.getDataFolder(), "data");
    	if(!folder.exists())
    		folder.mkdirs();
    	
    	String fileName = loc.getWorld().getName() + "_" + Math.round(loc.getX()) + "_" + Math.round(loc.getY()) + "_" + Math.round(loc.getZ()) + ".yml";
    	File file = new File(folder, fileName);
    	Yaml yaml = new Yaml();

    	// create serializableitemstack array from items
    	SerializableItemStack[] sItems = new SerializableItemStack[items.length];
    	for(int i=0; i<items.length; i++)
    		if(items[i] == null)
    			sItems[i] = null;
    		else
    			sItems[i] = new SerializableItemStack((CraftItemStack)items[i]);
    	
		try 
		{
			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(yaml.dump(sItems));
			out.close();
			fos.close();
		} 
		catch (Exception e) 
		{
			Plugin.getLogger().log(Level.WARNING, e.getMessage());
		}

    }
    
    public void removeInventory(Location loc)
    {
    	File folder = new File(Plugin.getDataFolder(), "data");
    	if(!folder.exists())
    	{
    		folder.mkdirs();
    		return;
    	}

    	String fileName = loc.getWorld().getName() + "_" + Math.round(loc.getX()) + "_" + Math.round(loc.getY()) + "_" + Math.round(loc.getZ()) + ".yml";
    	File file = new File(folder, fileName);
    	
    	if(!file.exists())
    		return;

    	file.delete();
    	
    }

    public Boolean canAccessReplicator(Sign sign, Player player)
    {
    	Boolean retval = false;
    	String playerName = player.getName();
    	
		if(Replicator.Config.isAuthorized(playerName, "access") || Replicator.Config.isAuthorized(playerName, "access.*"))
			retval = true;
		else
		{
			// see if the sign is named, check for more specific permission
			String signName = ReplicatorUtil.getSignName(sign);
			if(signName != null)
				if(Replicator.Config.isAuthorized(playerName, "access." + signName))
					retval = true;
		}
    	
    	return retval;
    }
}
