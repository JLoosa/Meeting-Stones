package me.jrl1004.java.meetingstones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class MeetingStones extends JavaPlugin implements Listener {

	private Set<Stone> stones;

	@Override
	public void onDisable() {
		if (stones.isEmpty()) return;
		Map<World, List<String>> sd = new HashMap<World, List<String>>();
		for (Stone s : stones)
			if (sd.containsKey(s.getworld())) sd.get(s.getworld()).add(s.getSaveString());
			else
				sd.put(s.getworld(), new ArrayList<String>(Arrays.asList(s.getSaveString())));
		for (World w : sd.keySet())
			getConfig().set(w.getName(), sd.get(w));
		saveConfig();
		stones.clear();
		stones = null;
		super.onDisable();
	}

	@Override
	public void onEnable() {
		stones = new HashSet<Stone>();
		Bukkit.getPluginManager().registerEvents(this, this);
		if (getConfig() == null)
			saveDefaultConfig();
		if (getConfig().getKeys(false).size() == 0) return;
		Set<String> keys = getConfig().getKeys(false);
		for (String s : keys) {
			World world = Bukkit.getWorld(s);
			List<String> list = getConfig().getStringList(s);
			for (String data : list)
				stones.add(new Stone(world, data.split(":")[0], data.split(":")[1]));
		}
		super.onEnable();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(command.getName().equalsIgnoreCase("meetingstones") || command.getName().equalsIgnoreCase("mswarp"))) return false;
		if (!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		switch (command.getName().toLowerCase()) {
			case "meetingstones":
				if (!(player.hasPermission("meetingstones.admin") || player.isOp())) return false;
				if (args.length == 0) {
					player.sendMessage(" /MeetingStones <Create | Clear>");
					return true;
				}
				ItemStack stack = player.getItemInHand();
				if (stack == null || stack.getType() == Material.AIR || !stack.getType().isBlock()) {
					player.sendMessage("Please hold the block you would like to use");
					return true;
				}
				if (args[0].equalsIgnoreCase("create")) {
					ItemMeta meta = stack.getItemMeta();
					List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
					lore.add("MeetingStone");
					meta.setLore(lore);
					stack.setItemMeta(meta);
					player.sendMessage("Held block is now a meeting stone");
				}
				if (args[0].equalsIgnoreCase("clear")) {
					ItemMeta m = stack.getItemMeta();
					m.setLore(new ArrayList<String>());
					stack.setItemMeta(m);
					player.sendMessage("Held block reset");
				}
				break;
			case "mswarp":
				if (!(player.hasPermission("meetingstones.warp") || player.isOp())) return false;
				if (args.length == 0) {
					player.sendMessage("/mswarp <name>");
					return true;
				}
				Stone stone = findStone(args[0]);
				if (stone == null) {
					player.sendMessage("Stone Not Found");
					return true;
				}
				player.teleport(stone.getLocation().add(0.5, 1, 0.5));
				break;
		}
		return super.onCommand(sender, command, label, args);
	}

	private Stone findStone(String string) {
		if (stones.isEmpty())
			return null;
		for (Stone s : stones)
			if (s.getName().equalsIgnoreCase(string))
				return s;
		return null;
	}

	private Stone findStone(Location location) {
		if (stones.isEmpty())
			return null;
		for (Stone s : stones)
			if (s.getLocation().equals(location))
				return s;
		return null;
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (!event.getItemInHand().getItemMeta().getLore().contains("MeetingStone")) return;
		Stone s = new Stone(event.getBlockPlaced(), getNewName(event.getItemInHand().getItemMeta().getDisplayName()));
		event.getBlockPlaced().setMetadata("MeetingStone", new FixedMetadataValue(this, s.getName()));
		event.getPlayer().sendMessage("MeetingStone created: " + s.getName());
		stones.add(s);
	}

	private String getNewName(String displayName) {
		if (displayName == null || displayName.length() == 0) return "STONE_" + stones.size();
		if (stones.isEmpty()) return displayName;
		for (Stone s : stones)
			if (s.getName().equalsIgnoreCase(displayName)) return "STONE_" + stones.size();
		return displayName;
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Stone s = findStone(event.getBlock().getLocation());
		if (s == null) return;
		stones.remove(s);
		event.getPlayer().sendMessage("MeetingStone removed");
	}
}
