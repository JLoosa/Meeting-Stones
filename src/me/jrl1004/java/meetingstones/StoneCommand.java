package me.jrl1004.java.meetingstones;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StoneCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(command.getName().equalsIgnoreCase("meetingstones") || command.getName().equalsIgnoreCase("mswarp"))) return false;
		if (!(sender instanceof Player)) {
			messageColored(sender, "Only players may use MeetingStones commands");
			return true;
		}
		Player player = (Player) sender;
		switch (command.getName().toLowerCase()) {
			case "meetingstones":
				adminCommand(player, args);
				break;
			case "mswarp":
				warpCommand(player, args);
				break;
		}
		return true;
	}

	private void adminCommand(Player player, String... args) {
		if (!(player.hasPermission("meetingstones.admin") || player.isOp())) {
			messageColored(player, "You do not have permission to do this");
			return;
		}
		if (args.length == 0) {
			messageColored(player, "/MeetingStones <Create | Clear>");
			return;
		}
		ItemStack stack = player.getItemInHand();
		if (stack == null || stack.getType() == Material.AIR || !stack.getType().isBlock()) {
			messageColored(player, "Please hold the block you would like to use");
			return;
		}
		if (args[0].equalsIgnoreCase("create")) {
			ItemMeta meta = stack.getItemMeta();
			List<String> lore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
			if (!lore.contains("MeetingStone"))
				lore.add("MeetingStone");
			meta.setLore(lore);
			stack.setItemMeta(meta);
			messageColored(player, "Held block is now a meeting stone");
		}
		if (args[0].equalsIgnoreCase("clear")) {
			ItemMeta m = stack.getItemMeta();
			List<String> lore = m.getLore() == null ? new ArrayList<String>() : m.getLore();
			while (lore.contains("MeetingStone"))
				lore.remove("MeetingStone");
			m.setLore(lore);
			stack.setItemMeta(m);
			player.setItemInHand(stack);
			messageColored(player, "Held block reset");
		}
	}

	private void warpCommand(Player player, String... args) {
		if (!(player.hasPermission("meetingstones.warp") || player.isOp())) {
			messageColored(player, "You do not have permission to do this");
			return;
		}
		if (args.length == 0) {
			messageColored(player, "/MSWarp <Name>");
			return;
		}
		String name = args[0];
		if (args.length > 1)
			for (int i = 1; i < args.length; i++)
				name += " " + args[i];
		Stone stone = MeetingStones.getInstance().findStone(name);
		if (stone == null) {
			messageColored(player, "Stone not found");
			return;
		}
		player.teleport(stone.getLocation().add(0.5, 1, 0.5));
		messageColored(player, "Teleported to MeetingStone \"" + stone.getName() + "\"");
	}
	
	private String prefix = ChatColor.GREEN + "MeetingStones > " + ChatColor.AQUA;
	
	private void messageColored(CommandSender sender, String... messages) {
		if(sender == null) return;
		if(messages.length == 0) return;
		for(String msg : messages)
			sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}
}
