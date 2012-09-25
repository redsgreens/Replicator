package redsgreens.Replicator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;

import de.bananaco.bookapi.lib.Book;
import de.bananaco.bookapi.lib.CraftBookBuilder;

public class SerializableItemStack implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Integer type = null;
    private Integer amount = null;
    private Short damage = null;
    private Byte data = null;

    private HashMap<Integer, Integer> enchants = new HashMap<Integer, Integer>();
    
    private HashMap<String, Object> tag = new HashMap<String, Object>();
    
    public SerializableItemStack(CraftItemStack is)
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
    	
    	if(type == Material.WRITTEN_BOOK.ordinal() || type == Material.BOOK_AND_QUILL.ordinal())
    	{
    		Book book = new CraftBookBuilder().getBook(is);
    		tag.put("author", book.getAuthor());
    		tag.put("title", book.getTitle());
    		tag.put("pages", book.getPages());
    	}
    }
    
    public CraftItemStack getItemStack()
    {
    	CraftItemStack retval = new CraftItemStack(type, amount, damage, data);
    	
    	Iterator<Integer> itr = enchants.keySet().iterator();
    	while(itr.hasNext())
    	{
    		Integer e = itr.next();
    		retval.addUnsafeEnchantment(Enchantment.getById(e), enchants.get(e));
    	}

    	if(type == Material.WRITTEN_BOOK.ordinal() || type == Material.BOOK_AND_QUILL.ordinal())
    	{
    		Book book = new CraftBookBuilder().getBook(retval);
    		book.setAuthor((String)tag.get("author"));
    		book.setTitle((String)tag.get("title"));
    		book.setPages((String[])tag.get("pages"));
    		
    		retval = book.getItemStack();
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
