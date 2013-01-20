package redsgreens.Replicator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ReplicatorListener implements Listener {

	private Replicator Plugin = null;

	// detect placing of a new replicator sign and save the inventory of the chest(if allowed)
    @EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event)
	// looks for a new SupplySign and tests it for validity
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;

		final Block signBlock = event.getBlock();

		// only proceed if it's a new sign
		if (event.getLine(0).equalsIgnoreCase("[" + Replicator.Config.SignTag +"]") || event.getLine(0).equals("§1[" + Replicator.Config.SignTag + "]"))
		{
			event.setCancelled(true);
			
			Player player = event.getPlayer();
			
			// bail if they don't have permission
			if (!Replicator.Config.isAuthorized(player, "create"))
			{
				signBlock.setType(Material.AIR);
				signBlock.getWorld().dropItemNaturally(signBlock.getLocation(), new ItemStack(Material.SIGN, 1));
				
				if(Replicator.Config.ShowErrorsInClient)
					player.sendMessage("§cReplicator: You do not have permission to place this sign.");

				return;
			}
			
			Boolean validSign = false;
			
			Block chest = null;

			if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.WEST)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)5);
				chest = signBlock.getRelative(BlockFace.WEST);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.EAST)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)4);
				chest = signBlock.getRelative(BlockFace.EAST);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.NORTH)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)3);
				chest = signBlock.getRelative(BlockFace.NORTH);
			}
			else if(ReplicatorUtil.isValidChest(signBlock.getRelative(BlockFace.SOUTH)))
			{
				validSign = true;
				signBlock.setType(Material.WALL_SIGN);
				signBlock.setData((byte)2);
				chest = signBlock.getRelative(BlockFace.SOUTH);
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
				
				Material material = chest.getType();
				if(material == Material.CHEST)
					Replicator.Config.saveInventory(((Chest)chest.getState()).getInventory().getContents(), chest.getLocation());
				else
					Replicator.Config.saveInventory(((Dispenser)chest.getState()).getInventory().getContents(), chest.getLocation());
				
				final Sign sign = (Sign)signBlock.getState();
				final String[] lines = event.getLines();
				Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
				    public void run() {

						// set the first line blue if it's not already
						if(!sign.getLine(0).equals("§1[" + Replicator.Config.SignTag + "]"))
							sign.setLine(0, "§1[" + Replicator.Config.SignTag + "]");

						sign.setLine(1, lines[1]);
						sign.setLine(2, lines[2]);
						sign.setLine(3, lines[3]);
				    	
				    	sign.update(true);
				    }
				}, 0);
			}
			else
			{
				signBlock.setType(Material.AIR);
				signBlock.getWorld().dropItemNaturally(signBlock.getLocation(), new ItemStack(Material.SIGN, 1));
				
				if(Replicator.Config.ShowErrorsInClient)
					player.sendMessage("§cReplicator: Sign cannot be placed.");

				return;
			}
			
		}
		
	}

    
    // detect opening of Replicator inventory and reset it to the stored contents of the chest
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
    	if(event.isCancelled()) return;
    	
    	Object holderObj = event.getInventory().getHolder();
    	if(holderObj instanceof Chest || holderObj instanceof DoubleChest || holderObj instanceof Dispenser)
    	{
    		Block chest;
    		if(holderObj instanceof Chest)
    			chest = ((Chest)holderObj).getBlock();
    		else if(holderObj instanceof DoubleChest)
    			chest = ((DoubleChest)holderObj).getWorld().getBlockAt(((DoubleChest)holderObj).getLocation());
    		else chest = ((Dispenser)holderObj).getBlock();
    			
    		Sign sign = ReplicatorUtil.getAttachedSign(chest); 
    		if(sign != null)
    		{
    			Player player = Plugin.getServer().getPlayer(event.getPlayer().getName());
    			
    			if(!Replicator.Config.canAccessReplicator(sign, player))
    			{
    				event.setCancelled(true);
    				if(Replicator.Config.ShowErrorsInClient)
    					player.sendMessage("§cReplicator: You do not have permission to open this chest.");
    				return;
    			}

    			Location loc = chest.getLocation();
    			
    			ItemStack[] items = Replicator.Config.loadInventory(loc);
    			if(items == null && ReplicatorUtil.isDoubleChest(chest))
    				items = Replicator.Config.loadInventory(ReplicatorUtil.findOtherHalfofChest(chest).getLocation());
    			
    			if(items != null)
    				event.getInventory().setContents(ReplicatorUtil.cloneItemStackArray(items));
    			else
    				Plugin.getLogger().info("Could not load inventory for Replicator at " + chest.getLocation());
    		}
    	}
    }


    // reset chest contents on inventory close so the contents are not lost if the sign is destroyed
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event)
    {
    	Object holderObj = event.getInventory().getHolder();
    	if(holderObj instanceof Chest || holderObj instanceof DoubleChest || holderObj instanceof Dispenser)
    	{
    		Block chest;
    		if(holderObj instanceof Chest)
    			chest = ((Chest)holderObj).getBlock();
    		else if(holderObj instanceof DoubleChest)
    			chest = ((DoubleChest)holderObj).getWorld().getBlockAt(((DoubleChest)holderObj).getLocation());
    		else chest = ((Dispenser)holderObj).getBlock();
    			
    		Sign sign = ReplicatorUtil.getAttachedSign(chest); 
    		if(sign != null)
    		{
    			Location loc = chest.getLocation();
    			
    			ItemStack[] items = Replicator.Config.loadInventory(loc);
    			if(items == null && ReplicatorUtil.isDoubleChest(chest))
    				items = Replicator.Config.loadInventory(ReplicatorUtil.findOtherHalfofChest(chest).getLocation());
    			
    			if(items != null)
    				event.getInventory().setContents(ReplicatorUtil.cloneItemStackArray(items));
    			else
    				Plugin.getLogger().info("Could not load inventory for Replicator at " + chest.getLocation());
    		}
    		
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
		Player player = event.getPlayer();
		
		if(blockMaterial == Material.WALL_SIGN || blockMaterial == Material.SIGN_POST)
		{
			Sign sign = (Sign)block.getState();
			if (ReplicatorUtil.isReplicatorSign(sign))
			{
				if(!Replicator.Config.isAuthorized(player, "destroy"))
				{
					event.setCancelled(true);
					if(Replicator.Config.ShowErrorsInClient)
						player.sendMessage("§cReplicator: You do not have permission to destroy this sign.");
					return;
				}
				else
				{
					Block b = ReplicatorUtil.getAttachedChest(block);
					Location loc = b.getLocation();
					if(b != null)
					{
						Replicator.Config.removeInventory(loc);
						
						if(ReplicatorUtil.isDoubleChest(b))
							Replicator.Config.removeInventory(ReplicatorUtil.findOtherHalfofChest(b).getLocation());
					}
				}
			}
		}
		else if (blockMaterial == Material.CHEST || blockMaterial == Material.DISPENSER)
		{
			Sign sign = ReplicatorUtil.getAttachedSign(event.getBlock());
			if(sign != null)
			{
				event.setCancelled(true);
				if(Replicator.Config.ShowErrorsInClient)
					player.sendMessage("§cReplicator: Chest cannot be broken with sign attached.");
				return;
			}
		}

	}

    @EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event)
    {
    	if(event.getAction() != Action.RIGHT_CLICK_BLOCK || event.isCancelled() == true)
    		return;
    	
    	Block block = event.getClickedBlock();
    	Material material = block.getType();
    	
    	if(material == Material.CHEST || material == Material.DISPENSER)
    	{
    		Sign sign = ReplicatorUtil.getAttachedSign(block);
    		if(sign != null)
    		{
    			Player player = Plugin.getServer().getPlayer(event.getPlayer().getName());

    			if(!Replicator.Config.canAccessReplicator(sign, player))
    			{
    				event.setCancelled(true);
    				if(Replicator.Config.ShowErrorsInClient)
    					player.sendMessage("§cReplicator: You do not have permission to open this chest.");
    			}

    		}

    	}
    	
    }

	// refill the dispenser after it fires
    @EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockDispense(BlockDispenseEvent event)
    {
    	Block block = event.getBlock();
		Sign sign = ReplicatorUtil.getAttachedSign(block); 
		if(sign != null)
		{
	    	Location loc = block.getLocation();
	    	final Dispenser dispenser = (Dispenser)block.getState();

			final ItemStack[] items = Replicator.Config.loadInventory(loc);
			if(items != null)
			{
				// reload the inventory now
				dispenser.getInventory().setContents(ReplicatorUtil.cloneItemStackArray(items));

				// reload the inventory after the dispense has happened
				Plugin.getServer().getScheduler().scheduleSyncDelayedTask(Plugin, new Runnable() {
				    public void run() {
						dispenser.getInventory().setContents(ReplicatorUtil.cloneItemStackArray(items));
				    }
				}, 1);

			}
			else
				Plugin.getLogger().info("Could not load inventory for Replicator at " + loc);

		}
    	
    }
    
    ReplicatorListener(Replicator plugin)
    {
    	Plugin = plugin;
    }
}
