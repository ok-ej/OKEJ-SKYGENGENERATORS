package pl.okej.okejspaceskygengenerators.generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import pl.okej.okejspaceskygengenerators.Main;

import java.util.Collection;

public class MoneyItem {

    public static final String MONEY_KEY = "skygenerators.money";
    private static final double MERGE_RADIUS = 1.0; // Radius to check for nearby money items to merge

    public static Item spawn(Location location, double amount) {
        // Center the location on the block
        Location centerLoc = location.clone();
        centerLoc.setX(centerLoc.getBlockX() + 0.5);
        centerLoc.setY(centerLoc.getBlockY() + 0.5);
        centerLoc.setZ(centerLoc.getBlockZ() + 0.5);

        // Check for nearby money items to merge with
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(centerLoc, MERGE_RADIUS, MERGE_RADIUS, MERGE_RADIUS);
        double totalAmount = amount;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isMoneyItem(item.getItemStack())) {
                    totalAmount += getMoneyAmount(item.getItemStack());
                    entity.remove();
                }
            }
        }

        ItemStack item = createMoneyItem(totalAmount);

        Item droppedItem = location.getWorld().dropItem(centerLoc, item);
        droppedItem.setVelocity(new Vector(0, 0.1, 0));
        droppedItem.setCustomName(item.getItemMeta().getDisplayName());
        droppedItem.setCustomNameVisible(true);
        droppedItem.setGlowing(true);

        return droppedItem;
    }

    public static double getTotalMoneyNearby(Location location, double radius) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        double total = 0;

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isMoneyItem(item.getItemStack())) {
                    total += getMoneyAmount(item.getItemStack());
                }
            }
        }

        return total;
    }

    public static void removeNearbyMoneyItems(Location location, double radius) {
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, radius, radius, radius);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (isMoneyItem(item.getItemStack())) {
                    entity.remove();
                }
            }
        }
    }

    private static ItemStack createMoneyItem(double amount) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        String name = Main.getInstance().getConfigManager().getString("item.name", "&e&l$%amount%");
        int customModelData = Main.getInstance().getConfigManager().getInt("item.custom-model-data", 10000);

        name = Main.getInstance().getMessageUtils().formatMessage(name.replace("%amount%", String.format("%.2f", amount)));

        meta.setDisplayName(name);
        meta.setCustomModelData(customModelData);

        meta.getPersistentDataContainer().set(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE,
                amount
        );

        item.setItemMeta(meta);
        return item;
    }

    public static boolean isMoneyItem(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE
        );
    }

    public static double getMoneyAmount(ItemStack item) {
        if (!isMoneyItem(item)) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().getOrDefault(
                org.bukkit.NamespacedKey.fromString(MONEY_KEY, Main.getInstance()),
                PersistentDataType.DOUBLE,
                0.0
        );
    }
}