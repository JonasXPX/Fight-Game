package br.com.endcraft.fightevent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Lists;
/**
 * 
 * @author JonasXPX
 * 
 * Class responsible for loading the kits
 * From Kit TO json serializable
 * From json TO Kit usable Object
 *
 */
public class Kit {
	
	private ItemStack[] armor;
	private ItemStack[] contents;
	private String name;
	
	public ItemStack[] getArmor() {
		return armor;
	}
	public ItemStack[] getContents() {
		return contents;
	}
	
	public void setArmor(ItemStack[] armor) {
		this.armor = armor;
	}
	
	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static Kit fromJson(JSONObject json) {
		JSONArray jsonContents = json.getJSONArray("contents");
		JSONArray jsonArmor = json.getJSONArray("armor");
		Kit kit = new Kit();
		kit.setArmor(fromJson(jsonArmor));
		kit.setContents(fromJson(jsonContents));
		kit.setName(json.getString("name"));
		return kit;
	}
	
	private static ItemStack[] fromJson(JSONArray s) {
		List<ItemStack> content = Lists.newArrayList();
		for(Object o : s) {
			if(o instanceof JSONObject) {
				JSONObject itens = (JSONObject) o;
				Material material = Material.getMaterial(itens.getString("item"));
				List<String> lore = Arrays.asList(itens.getString("lore").split(";"));
				Map<Enchantment, Integer> encantamentos = new HashMap<>();
				for(Object en : itens.getJSONArray("encantos")) {
					JSONObject enchant = (JSONObject)en;
					encantamentos.put(Enchantment.getByName(enchant.getString("nome")), enchant.getInt("value"));
				}
				ItemStack item = new ItemStack(material);
				if(item.getType() != Material.AIR) {
					String customName = itens.getString("item_name");
					ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
					encantamentos.forEach((k,v) -> meta.addEnchant(k, v, true));
					meta.setLore(lore);
					meta.setDisplayName(customName);
					item.setItemMeta(meta);
					item.setDurability(Short.parseShort(itens.getString("durability")));
				}
				content.add(item);
			}
		}
		return content.toArray(new ItemStack[content.size()]);
	}
	
	public static JSONObject toJson(Kit kit) {
		JSONObject object = new JSONObject();
		object.put("contents", toArray(kit.getContents()));
		object.put("armor", toArray(kit.getArmor()));
		object.put("name", kit.getName());
		return object;
	}
	
	private static JSONArray toArray(ItemStack[] con) {
		JSONArray array = new JSONArray();
		for(ItemStack content : con) {
			if(content == null )
				continue;
		
			JSONObject item = new JSONObject();
			String name = content.getType().name();
			ItemMeta meta = content.getItemMeta();
			final StringBuilder lore = new StringBuilder();
			JSONArray encantos = new JSONArray();
			String item_name = "";
			if(content.hasItemMeta()) {
				item_name = meta.hasDisplayName() ? meta.getDisplayName() : "";
				if(meta.hasLore()) {
					List<String> l = meta.getLore();
					l.forEach(lores -> {
						lore.append(lores);
						lore.append(";");
					});
				}
				if(meta.hasEnchants()) {
					meta.getEnchants().forEach((k,v) ->{
						JSONObject en = new JSONObject();
						en.put("nome", k.getName());
						en.put("value", v);
						encantos.put(en);
					});
				}
			}
			
			item.put("encantos", encantos);
			item.put("item", name);
			item.put("lore", lore.toString());
			item.put("item_name", item_name);
			item.put("durability", Short.toString(content.getDurability()));

			array.put(item);
		}
		return array;
	}
	
	
	public static void addKit(Player player) {
		ItemStack[] armor = new ItemStack[] {new ItemStack(Material.DIAMOND_BOOTS), 
				new ItemStack(Material.DIAMOND_LEGGINGS), 
				new ItemStack(Material.DIAMOND_CHESTPLATE), 
				new ItemStack(Material.DIAMOND_HELMET)};
		ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
		
		Potion potion = new Potion(PotionType.STRENGTH);
		ItemStack apple = new ItemStack(Material.GOLDEN_APPLE, 2);
		
		for(ItemStack stacks : armor) {
			stacks.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
			stacks.addEnchantment(Enchantment.DURABILITY, 3);
		}
		
		axe.addEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
		sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		
		potion.setType(PotionType.STRENGTH);
		potion.setLevel(2);
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 240, 2), true);
		
		apple.setDurability((short) 1);
		
		player.getInventory().setArmorContents(armor);
		player.getInventory().addItem(axe, sword, apple, potion.toItemStack(2));
		
	}
	public void put(Player player) {
		player.getInventory().setArmorContents(getArmor());
		player.getInventory().setContents(getContents());
	}
	
}
