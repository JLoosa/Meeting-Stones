package me.jrl1004.java.meetingstones;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Stone {

	private String name;
	private Location location;

	public Stone(Block block, String name) {
		this.name = name;
		this.location = block.getLocation();
	}

	public Stone(World world, String name, String vector) {
		this.location = fromString(vector).toLocation(world);
		this.name = name;
	}

	private Vector fromString(String s) {
		if (!s.contains(",")) return null;
		String[] split = s.split(",");
		return new Vector(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	public String getName() {
		return this.name;
	}

	public Location getLocation() {
		return this.location;
	}

	public World getworld() {
		return this.location.getWorld();
	}

	public String getStorableVector() {
		Vector vector = location.toVector();
		return vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ();
	}

	public String getSaveString() {
		return name + ":" + getStorableVector();
	}

	public boolean isAtLocation(Location loc) {
		if (!loc.getWorld().equals(location.getWorld())) return false;
		if (loc.getBlockX() != location.getBlockX()) return false;
		if (loc.getBlockY() != location.getBlockY()) return false;
		if (loc.getBlockZ() != location.getBlockZ()) return false;
		return true;
	}

	public boolean verifyData() {
		if (location == null) return false;
		if (location.getWorld() == null) return false;
		if (name == null || name.length() == 0) return false;
		return true;
	}
}
