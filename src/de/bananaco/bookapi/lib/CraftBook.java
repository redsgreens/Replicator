package de.bananaco.bookapi.lib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;


public class CraftBook implements Book {
    
    private final CraftItemStack s;
	private NBTTagCompound tag;
    
    public CraftBook(CraftItemStack itemstack) throws Exception {
        if(itemstack.getType() == Material.WRITTEN_BOOK || itemstack.getType() == Material.BOOK_AND_QUILL) {
            //do nothing
        } else {
            throw new Exception("CraftItemStack not Material.WRITTEN_BOOK or Material.BOOK_AND_QUILL");
        }
        s = itemstack;
        tag = itemstack.getHandle().getTag();
        if(tag == null) {
            tag = new NBTTagCompound();
            s.getHandle().setTag(tag);
        }
    }

    @Override
    public boolean hasTitle() {
        return tag.hasKey("title");
    }

    @Override
    public boolean hasAuthor() {
        return tag.hasKey("author");
    }

    @Override
    public boolean hasPages() {
        return tag.hasKey("pages");
    }

    @Override
    public String getTitle() {
        return tag.getString("title");
    }

    @Override
    public String getAuthor() {
        return tag.getString("author");
    }

    @Override
    public String[] getPages() {
        NBTTagList list = (NBTTagList) tag.get("pages");
        String[] pages = new String[list.size()];
        for(int i=0; i<list.size(); i++) {
            pages[i] = ((NBTTagString) list.get(i)).data;
        }
        return pages;
    }
    
    @Override
    public List<String> getListPages() {
        NBTTagList list = (NBTTagList) tag.get("pages");
        List<String> pages = new ArrayList<String>();
        for(int i=0; i<list.size(); i++) {
            pages.add(((NBTTagString) list.get(i)).data);
        }
        return pages;
    }

    @Override
    public void setTitle(String title) {
        // sanity checking on the title
        if(title.length() > 16) {
            title = title.substring(0, 16);
        }
        tag.setString("title", title);
    }

    @Override
    public void setAuthor(String author) {
        // sanity checking on the author
        if(author.length() > 16) {
            author = author.substring(0, 16);
        }
        tag.setString("author", author);
    }

    @Override
    public void setPages(String[] pages) {
        NBTTagList list = new NBTTagList();
        int size = pages.length;
        for(int i=0; i<size; i++) {
            String page = pages[i];
            // sanity checking on the page
            if(page.length() > 256) {
                page = page.substring(0, 256);
            }
            if(page != null && !page.equals("") && !page.isEmpty()) {
                NBTTagString p = new NBTTagString(page);
                p.setName(page);
                p.data = page;
                list.add(p);
            }
        }
        list.setName("pages");
        tag.set("pages", list);
    }

	@Override
	public void setPages(List<String> pages) {
		 NBTTagList list = new NBTTagList();
	        int size = pages.size();
	        for(int i=0; i<size; i++){
	        	String page = pages.get(i);
	            if(page.length() > 256) {
	            	page = page.substring(0, 256);
	            }
	            if(page != null && !page.equals("") && !page.isEmpty()) {
	                NBTTagString p = new NBTTagString(page);
	                p.setName(page);
	                p.data = page;
	                list.add(p);
	            }
	        }
	        list.setName("pages");
	        tag.set("pages", list);
	}
	
	@Override
	public CraftItemStack getItemStack(){
		return s;
	}

}
