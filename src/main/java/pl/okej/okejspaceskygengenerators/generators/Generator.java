package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;
import pl.okej.okejspaceskygengenerators.Main;

public class Generator {

    private final String id;
    private final Location location;
    private final int interval;
    private final double amount;
    private boolean enabled;
    private BukkitTask task;

    public static Generator fromConfig(String id, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        ConfigurationSection locationSection = section.getConfigurationSection("location");
        if (locationSection == null) {
            return null;
        }

        String worldName = locationSection.getString("world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            Main.getInstance().getLogger().warning("Świat '" + worldName + "' nie został znaleziony dla gene '" + id + "'!");
            return null;
        }

        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        Location location = new Location(world, x, y, z);

        int interval = section.getInt("interval", 300);
        double amount = section.getDouble("amount", 1000);
        boolean enabled = section.getBoolean("enabled", true);

        return new Generator(id, location, interval, amount, enabled);
    }

    public Generator(String id, Location location, int interval, double amount, boolean enabled) {
        this.id = id;
        this.location = location;
        this.interval = interval;
        this.amount = amount;
        this.enabled = enabled;
    }

    public void start() {
        if (!enabled) {
            return;
        }

        stop();

        task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            generateMoney();
        }, 20L, interval * 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void generateMoney() {
        if (!enabled) {
            return;
        }

        if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return;
        }

        double finalAmount = amount;
        if (Main.getInstance().getGenBoostManager().isActive()) {
            finalAmount *= Main.getInstance().getGenBoostManager().getMultiplier();
        }

        MoneyItem.spawn(location, finalAmount);
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getInterval() {
        return interval;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }
}