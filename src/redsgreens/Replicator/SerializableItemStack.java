package redsgreens.Replicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        		PotionMeta pMeta = (PotionMeta)meta;

        		if(pMeta.getCustomEffects().size() > 0)
        		{
        			List<PotionEffect> effectsIn = pMeta.getCustomEffects();
        			Iterator<PotionEffect> pItr = effectsIn.iterator();
        			ArrayList<HashMap<String, Integer>> effectsOut = new ArrayList<HashMap<String, Integer>>();
        			
            		while(pItr.hasNext())
            		{
            			PotionEffect effectIn = pItr.next();
            			HashMap<String, Integer> effectOut = new HashMap<String, Integer>();
            			
            			effectOut.put("aplifier", effectIn.getAmplifier());
            			effectOut.put("duration", effectIn.getDuration());
            			effectOut.put("type", effectIn.getType().getId());
            			
            			effectsOut.add(effectOut);
            		}
            		
            		tag.put("effects", effectsOut);
        		}

        	}
        	else if (meta instanceof SkullMeta)
        	{
        		SkullMeta sMeta = (SkullMeta)meta;

        		tag.put("owner", sMeta.getOwner());
        	}
        	else if(meta instanceof FireworkMeta)
        	{
        		FireworkMeta fMeta = (FireworkMeta)meta;
        		List<FireworkEffect> effectsIn = fMeta.getEffects();
        		ArrayList<HashMap<String, Object>> effectsOut = new ArrayList<HashMap<String, Object>>(); 
        		
        		Iterator<FireworkEffect> effectItr = effectsIn.iterator();
        		while(effectItr.hasNext())
        			effectsOut.add(serializeFireworkEffect(effectItr.next()));

        		tag.put("effects", effectsOut);
        	}
        	else if(meta instanceof FireworkEffectMeta)
        	{
        		FireworkEffectMeta feMeta = (FireworkEffectMeta)meta;
        		
    			tag.put("effect", serializeFireworkEffect(feMeta.getEffect()));
        	}
        	else if(meta instanceof EnchantmentStorageMeta)
        	{
        		EnchantmentStorageMeta esMeta = (EnchantmentStorageMeta)meta;
        		
        		Map<Enchantment, Integer> seMap = esMeta.getStoredEnchants();
                Iterator<Enchantment> seItr = seMap.keySet().iterator();
                HashMap<Integer, Integer> sEnchants = new HashMap<Integer, Integer>();
                
                while(seItr.hasNext())
                {
                    Enchantment e = seItr.next();
                    sEnchants.put(e.getId(), seMap.get(e));
                }
                
                tag.put("storedenchants", sEnchants);
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
    			if(tag.containsKey("effects"))
    			{
    				PotionMeta pMeta = (PotionMeta)meta;
    				
    				ArrayList<HashMap<String, Integer>> effectsIn = (ArrayList<HashMap<String, Integer>>)tag.get("effects");
    				Iterator<HashMap<String, Integer>> eItr = effectsIn.iterator();
    				while(eItr.hasNext())
    				{
    					HashMap<String, Integer> effectIn = eItr.next();
    					PotionEffect effectOut = new PotionEffect(PotionEffectType.getById((Integer)effectIn.get("type")), (Integer)effectIn.get("duration"), (Integer)effectIn.get("amplifier"));
    					
    					pMeta.addCustomEffect(effectOut, true);
    				}
    				
    				retval.setItemMeta(pMeta);
    			}
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
    		else if(material == Material.FIREWORK_CHARGE)
    		{
    			if(tag.containsKey("effect"))
    			{
    				FireworkEffectMeta feMeta = (FireworkEffectMeta)meta;
    				HashMap<String, Object> effectIn = (HashMap<String, Object>)tag.get("effect");
    				
    				FireworkEffect effect = deserializeFireworkEffect(effectIn);
    				
    				if(effect != null)
    				{
    					feMeta.setEffect(effect);
    				    retval.setItemMeta(feMeta);
    				}
    			}
    		}
    		else if(material == Material.FIREWORK)
    		{
    			if(tag.containsKey("effects"))
    			{
    				FireworkMeta fMeta = (FireworkMeta)meta;
    				
    				ArrayList<HashMap<String, Object>> effectsIn = (ArrayList<HashMap<String, Object>>)tag.get("effects");
    				Iterator<HashMap<String, Object>> eItr = effectsIn.iterator();
    				while(eItr.hasNext())
    				{
    					FireworkEffect e = deserializeFireworkEffect(eItr.next());
    					
    					if(e != null)
    						fMeta.addEffect(e);
    				}
    				
    				retval.setItemMeta(fMeta);
    			}
    		}
    		else if(material == Material.ENCHANTED_BOOK)
    		{
    			if(tag.containsKey("storedenchants"))
    			{
    				EnchantmentStorageMeta esMeta = (EnchantmentStorageMeta)meta;
    				HashMap<Integer, Integer> sEnchants = (HashMap<Integer, Integer>)tag.get("storedenchants");
    				Iterator<Integer> seItr = sEnchants.keySet().iterator();
    				while(seItr.hasNext())
    				{
    					Integer e = seItr.next();
    					esMeta.addEnchant(Enchantment.getById(e), sEnchants.get(e), true);
    				}
    				retval.setItemMeta(esMeta);
    			}
    		}
    		else
    			retval.setItemMeta(meta);
    		
    	}
    	
    	return retval;
    }
    
	@SuppressWarnings("unchecked")
	FireworkEffect deserializeFireworkEffect(HashMap<String, Object> effectIn)
	{
		FireworkEffect retval = null;
		
		try
		{
			Builder builder = FireworkEffect.builder();
			
			if(effectIn.containsKey("flicker"))
				builder = builder.flicker((Boolean)effectIn.get("flicker"));

			if(effectIn.containsKey("trail"))
				builder = builder.trail((Boolean)effectIn.get("trail"));

			if(effectIn.containsKey("colors"))
			{
				List<Integer> colorsIn = (List<Integer>)effectIn.get("colors");
				ArrayList<Color> colorsOut = new ArrayList<Color>();
				Iterator<Integer> colorItr = colorsIn.iterator();
				while(colorItr.hasNext())
					colorsOut.add(Color.fromRGB(colorItr.next()));

				builder = builder.withColor(colorsOut);
			}

			if(effectIn.containsKey("fadecolors"))
			{
				List<Integer> fadeColorsIn = (List<Integer>)effectIn.get("fadecolors");
				ArrayList<Color> fadeColorsOut = new ArrayList<Color>();
				Iterator<Integer> fadeColorItr = fadeColorsIn.iterator();
				while(fadeColorItr.hasNext())
					fadeColorsOut.add(Color.fromRGB(fadeColorItr.next()));
				
				builder = builder.withFade(fadeColorsOut);
			}
			
			retval = builder.build();
		}
		catch(Exception e) {}
		
		return retval;
	}

	HashMap<String, Object> serializeFireworkEffect(FireworkEffect effectIn)
	{
		HashMap<String, Object> effectOut = new HashMap<String, Object>();
		
		effectOut.put("flicker", effectIn.hasFlicker());
		effectOut.put("trail", effectIn.hasTrail());
		
		List<Color> colorsIn = effectIn.getColors();
		ArrayList<Integer> colorsOut = new ArrayList<Integer>();
		Iterator<Color> colorItr = colorsIn.iterator();
		while(colorItr.hasNext())
			colorsOut.add(colorItr.next().asRGB());
		effectOut.put("colors", colorsOut);
		
		List<Color> fadeColorsIn = effectIn.getFadeColors();
		ArrayList<Integer> fadeColorsOut = new ArrayList<Integer>();
		Iterator<Color> fadeColorItr = fadeColorsIn.iterator();
		while(fadeColorItr.hasNext())
			fadeColorsOut.add(fadeColorItr.next().asRGB());
		effectOut.put("fadecolors", fadeColorsOut);
		
		return effectOut;
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
