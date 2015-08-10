package me.jrl1004.java.meetingstones;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class StoneListener implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		ItemStack stack = event.getItemInHand();
		if (!stack.getItemMeta().getLore().contains("MeetingStone")) return;
		Stone s = new Stone(event.getBlockPlaced(), MeetingStones.getInstance().getNewName(stack.getItemMeta().getDisplayName()));
		MeetingStones.getInstance().stones.add(s);
		event.getPlayer().sendMessage("MeetingStone created: " + s.getName());
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Stone s = MeetingStones.getInstance().findStone(event.getBlock().getLocation());
		if (s == null) return;
		MeetingStones.getInstance().stones.remove(s);
		event.getPlayer().sendMessage("MeetingStone removed");
	}
}
