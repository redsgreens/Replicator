package redsgreens.Replicator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

public class SerializableItemStack implements Serializable {

	private static final long serialVersionUID = 1L;

    private Integer type = null;
    private Integer amount = null;
    private Short damage = null;
    private Byte data = null;

    private HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
    
    private HashMap<String, Object> tag = new HashMap<String, Object>();
    
    public SerializableItemStack(ItemStack is)
    {
    	type = is.getTypeId();
    	amount = is.getAmount();
    	damage = is.getDurability();
    	data = is.getData().getData();

        Map<Enchantment, Integer> eMap = is.getEnchantments();
        Iterator<Enchantment> itr = eMap.keySet().iterator();
        while(itr.hasNext())
        {
            Enchantment e = itr.next();
            enchants.put(e.getId(), eMap.get(e));
        }
    	
    	if(is.hasItemMeta())
    	{
        	ItemMeta meta = is.getItemMeta();

        	if(meta.hasDisplayName())
        		tag.put("name", meta.getDisplayName());
        	
        	if(meta.hasLore())
        		tag.put("lore", meta.getLore());
        	
        	if(meta instanceof BookMeta)
        	{
        		BookMeta bMeta = (BookMeta)meta;
        		
        		if(bMeta.hasAuthor())
        			tag.put("author", bMeta.getAuthor());

        		if(bMeta.hasTitle())
        			tag.put("title", bMeta.getTitle());

        		if(bMeta.hasPages())
        			tag.put("pages", bMeta.getPages());
        		
        	}
        	else if (meta instanceof LeatherArmorMeta)
        	{
        		LeatherArmorMeta laMeta = (LeatherArmorMeta)meta;
        		
        		tag.put("color", laMeta.getColor().asRGB());
        	}
        	else if (meta instanceof PotionMeta)
        	{
//TODO: serializing potion effects is a PITA
/*
        		PotionMeta pMeta = (PotionMeta)meta;

        		if(pMeta.getCustomEffects().size() > 0)
        		{
        			
        			Iterator<PotionEffect> pItr = pMeta.getCustomEffects().iterator();
            		while(pItr.hasNext())
            		{
            			PotionEffect p = pItr.next();
            		}
        		}
*/
        	}
        	else if (meta instanceof SkullMeta)
        	{
        		SkullMeta sMeta = (SkullMeta)meta;

        		tag.put("owner", sMeta.getOwner());
        	}
        	
    	}    	
    }
    
	@SuppressWarnings("unchecked")
	public ItemStack getItemStack()
    {
    	@SuppressWarnings("deprecation")
		ItemStack retval = new ItemStack(type, amount, damage, data);

        Iterator<Integer> itr = enchants.keySet().iterator();
        while(itr.hasNext())
        {
            Integer e = itr.next();
            retval.addUnsafeEnchantment(Enchantment.getById(e), enchants.get(e));
        }
        
    	ItemMeta meta = retval.getItemMeta();

    	if(tag.size() > 0)
    	{
    		if(tag.containsKey("name"))
    			meta.setDisplayName((String)tag.get("name"));
    			
    		if(tag.containsKey("lore"))
    			meta.setLore((List<String>)tag.get("lore"));
    		
    		Material material = retval.getType();
   		
    		if(material == Material.BOOK_AND_QUILL || material == Material.WRITTEN_BOOK)
    		{
    			BookMeta bMeta = (BookMeta)meta;
    			
    			if(tag.containsKey("author"))
    				bMeta.setAuthor((String)tag.get("author"));
    			
    			if(tag.containsKey("title"))
    				bMeta.setTitle((String)tag.get("title"));

    			if(tag.containsKey("pages"))
    				bMeta.setPages((List<String>)tag.get("pages"));

    			retval.setItemMeta(bMeta);
    		}
    		else if(material == Material.LEATHER_BOOTS || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_HELMET || material == Material.LEATHER_LEGGINGS)
    		{
    			if(tag.containsKey("color"))
    			{
    				LeatherArmorMeta laMeta = (LeatherArmorMeta)meta;
    				laMeta.setColor(Color.fromRGB((Integer)tag.get("color")));
    				retval.setItemMeta(laMeta);
    			}
    		}
    		else if(material == Material.POTION)
    		{
//TODO: potions again...
    		}
    		else if(material == Material.SKULL || material == Material.SKULL_ITEM)
    		{
    			if(tag.containsKey("owner"))
    			{
    				SkullMeta sMeta = (SkullMeta)meta;
    				sMeta.setOwner((String)tag.get("owner"));
    				retval.setItemMeta(sMeta);
    			}
    			
    		}
    		else
    			retval.setItemMeta(meta);
    		
    	}
    	
    	return retval;
    }
    
    public SerializableItemStack()
    {
    }

    public Integer getType()
    {
    	return type;
    }
    public void setType(Integer t)
    {
    	type = t;
    }
    
    public Integer getAmount()
    {
    	return amount;
    }
    public void setAmount(Integer a)
    {
    	amount = a;
    }
    
    public Short getDamage()
    {
    	return damage;
    }
    public void setDamage(Short d)
    {
    	damage = d;
    }
    
    public Byte getData()
    {
    	return data;
    }
    public void setData(Byte d)
    {
    	data = d;
    }

    public HashMap<Integer, Integer> getEnchants()
    {
    	return enchants;
    }
    public void setEnchants(HashMap<Integer, Integer> e)
    {
    	enchants = e;
    }

    public HashMap<String, Object> getTag()
    {
        return tag;
    }
    public void setTag(HashMap<String, Object> t)
    {
        tag = t;
    }
}
