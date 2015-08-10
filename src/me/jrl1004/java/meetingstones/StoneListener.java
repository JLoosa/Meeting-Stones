package me.jrl1004.java.meetingstones;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class StoneListener implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItemInHand();
		if (stack.getItemMeta().getLore() == null || !stack.getItemMeta().getLore().contains("MeetingStone")) return;
		if (!(player.hasPermission("meetingstones.admin") || player.isOp())) {
			messageColored(player, "You do not have permission to place MeetingStones");
			event.setCancelled(true);
			return;
		}
		Stone s = new Stone(event.getBlockPlaced(), MeetingStones.getInstance().getNewName(stack.getItemMeta().getDisplayName()));
		MeetingStones.getInstance().stones.add(s);
		messageColored(event.getPlayer(), "MeetingStone created: " + s.getName());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Stone s = MeetingStones.getInstance().findStone(event.getBlock().getLocation());
		if (s == null) return;
		if (!(player.hasPermission("meetingstones.admin") || player.isOp())) {
			messageColored(player, "You do not have permission to remove MeetingStones");
			event.setCancelled(true);
			return;
		}
		MeetingStones.getInstance().stones.remove(s);
		messageColored(event.getPlayer(), "MeetingStone removed");
	}

	private String prefix = ChatColor.GREEN + "MeetingStones > " + ChatColor.AQUA;

	private void messageColored(CommandSender sender, String... messages) {
		if (sender == null) return;
		if (messages.length == 0) return;
		for (String msg : messages)
			sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', msg));
	}
}
