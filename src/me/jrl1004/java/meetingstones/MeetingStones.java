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
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MeetingStones extends JavaPlugin implements Listener {

	public Set<Stone> stones;
	private static MeetingStones instance;

	@Override
	public void onDisable() {
		instance = null;

		// Save the stones we currently have to file
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
		instance = this;
		stones = new HashSet<Stone>();
		Bukkit.getPluginManager().registerEvents(new StoneListener(), this);
		StoneCommand command_handler = new StoneCommand();
		getCommand("meetingstones").setExecutor(command_handler);
		getCommand("mswarp").setExecutor(command_handler);

		// Load in the old stones if we need to
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

	public static MeetingStones getInstance() {
		return instance;
	}

	public Stone findStone(String string) {
		if (stones.isEmpty())
			return null;
		for (Stone s : stones)
			if (s.getName().equalsIgnoreCase(string))
				return s;
		return null;
	}

	public Stone findStone(Location location) {
		if (stones.isEmpty())
			return null;
		for (Stone s : stones)
			if (s.isAtLocation(location))
				return s;
		return null;
	}

	public String getNewName(String displayName) {
		if (displayName == null || displayName.length() == 0) return "STONE_" + stones.size();
		if (stones.isEmpty()) return displayName;
		for (Stone s : stones)
			if (s.getName().equalsIgnoreCase(displayName)) return "STONE_" + stones.size();
		return displayName;
	}
}
