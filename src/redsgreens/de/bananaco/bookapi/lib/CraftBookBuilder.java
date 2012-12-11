package redsgreens.de.bananaco.bookapi.lib;

import org.bukkit.craftbukkit.v1_4_5.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * The implementation of BookBuilder
 */
public class CraftBookBuilder extends BookBuilder {
	
	static {
		// create the instance of the thing we need
		BookBuilder.instance = new CraftBookBuilder();
	}
	
	@Override
	public Book getBook(ItemStack itemstack) {
		try {
			return new CraftBook((CraftItemStack) itemstack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
