package redsgreens.Replicator;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ReplicatorListener implements Listener {

	private Replicator Plugin = null;
	private HashMap<Object, ItemStack[]> chestInventories = new HashMap<Object, ItemStack[]>();


	// detect placing of a new [Replicator] sign and save the inventory of the chest(if allowed)
    @EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event)
	// looks for a new SupplySign and tests it for validity
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;

		final Block signBlock = event.getBlock();

		// only proceed if it's a new sign
		if (event.getLine(0).equalsIgnoreCase("[Replicator]") || event.getLine(0).equals("§1[Replicator]"))
		{

			event.setCancelled(true);
			
			// bail if they don't have permission
			if (!Plugin.Config.isAuthorized(event.getPlayer(), "create"))
			{
				signBlock.setType(Material.AIR);
				signBlock.getWorld().dropItemNaturally(signBlock.getLocation(), new ItemStack(Material.SIGN, 1));
				return;
			}
			
			Boolean validSign = false;
			
			Block chest = null;
			
			if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.NORTH)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)5);
				chest = signBlock.getRelative(BlockFace.NORTH);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.SOUTH)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)4);
				chest = signBlock.getRelative(BlockFace.SOUTH);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.EAST)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)3);
				chest = signBlock.getRelative(BlockFace.EAST);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.WEST)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)2);
				chest = signBlock.getRelative(BlockFace.WEST);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.UP)))
			{
				validSign = true;
				chest = signBlock.getRelative(BlockFace.UP);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.DOWN)))
			{
				validSign = true;
				chest = signBlock.getRelative(BlockFace.DOWN);
			}
			if(validSign)
			{
				
				Plugin.Config.saveInventory(((Chest)chest.getState()).getInventory().getContents(), chest.getLocation());
				
				final Sign sign = (Sign)signBlock.getState();
				Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
				    public void run() {

						// set the first line blue if it's not already
						if(!sign.getLine(0).equals("§1[Replicator]"))
							sign.setLine(0, "§1[Replicator]");
				    	
				    	sign.update(true);
				    }
				}, 0);
			}
			else
			{
				signBlock.setType(Material.AIR);
				signBlock.getWorld().dropItemNaturally(signBlock.getLocation(), new ItemStack(Material.SIGN, 1));
				return;
			}
			
		}
		
	}

    
    // detect opening of Replicator inventory and reset it to the stored contents of the chest
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
    	Object holderObj = event.getInventory().getHolder();
    	if(holderObj instanceof Chest || holderObj instanceof DoubleChest)
    	{
    		Block chest;
    		if(holderObj instanceof Chest)
    			chest = ((Chest)holderObj).getBlock();
    		else
    			chest = ((DoubleChest)holderObj).getWorld().getBlockAt(((DoubleChest)holderObj).getLocation());
    			
    		if(ReplicatorUtil.getAttachedSign(chest) != null)
    		{
    			if(!Plugin.Config.isAuthorized(event.getPlayer().getName(), "access"))
    			{
    				event.setCancelled(true);
    				return;
    			}
    			
    			Location loc = chest.getLocation();
    			
    			if(!chestInventories.containsKey(loc))
    				chestInventories.put(loc, ReplicatorUtil.cloneItemStackArray(event.getInventory().getContents()));
    			
    			ItemStack[] items = chestInventories.get(loc);
    			if(items == null && ReplicatorUtil.isDoubleChest(chest))
    				items = chestInventories.get(ReplicatorUtil.findOtherHalfofChest(chest).getLocation());
    			
    			if(items != null)
    				event.getInventory().setContents(ReplicatorUtil.cloneItemStackArray(items));
    			else
    				System.out.println("Could not load inventory for Replicator at " + chest.getLocation());
    		}
    	}
    }


    // detect closing of Replicator inventory and reset it to the stored contents of the chest
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event)
    {
    	Object holderObj = event.getInventory().getHolder();
    	if(holderObj instanceof Chest || holderObj instanceof DoubleChest)
    	{
    		Block chest;
    		if(holderObj instanceof Chest)
    			chest = ((Chest)holderObj).getBlock();
    		else
    			chest = ((DoubleChest)holderObj).getWorld().getBlockAt(((DoubleChest)holderObj).getLocation());
 
    		Location loc = chest.getLocation();
    		ItemStack[] items = chestInventories.get(loc);
    		if(items != null)
    			event.getInventory().setContents(items);
    	}
    }
    
	// only allow players with permission to break a Replicator sign
    // do not permit breaking a replicator chest with sign attached
    @EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;
		
		Block block = event.getBlock();
		Material blockMaterial = block.getType();
		
		if(blockMaterial == Material.WALL_SIGN || blockMaterial == Material.SIGN_POST)
		{
			Sign sign = (Sign)block.getState();
			if (ReplicatorUtil.isReplicatorSign(sign))
			{
				if(!Plugin.Config.isAuthorized(event.getPlayer(), "destroy"))
				{
					event.setCancelled(true);
					return;
				}
				else
				{
					Chest chest = ReplicatorUtil.getAttachedChest(block);
					Location loc = chest.getLocation();
					if(chest != null & chestInventories.containsKey(loc))
					{
						chestInventories.remove(loc);
						
						if(ReplicatorUtil.isDoubleChest(chest.getBlock()))
							chestInventories.remove(ReplicatorUtil.findOtherHalfofChest(chest.getBlock()).getLocation());
					}
				}
			}
		}
		else if (event.getBlock().getType() == Material.CHEST)
		{
			Sign sign = ReplicatorUtil.getAttachedSign(event.getBlock());
			if(sign != null)
			{
				event.setCancelled(true);
				return;
			}
		}

	}

    ReplicatorListener(Replicator plugin)
    {
    	Plugin = plugin;
    }
}
