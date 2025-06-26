package pl.okej.okejspaceskygengenerators.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.okej.okejspaceskygengenerators.Main;
import pl.okej.okejspaceskygengenerators.generators.MoneyItem;

public class MoneyPickupListener implements Listener {

    private final Main plugin;
    private static final double PICKUP_RADIUS = 1.5;

    public MoneyPickupListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();

        if (MoneyItem.isMoneyItem(item)) {
            event.setCancelled(true);

            if (!player.hasPermission("okejgenerators.collect")) {
                plugin.getMessageUtils().sendMessage(player, plugin.getConfigManager().getMessage("no-permission"));
                return;
            }

            double totalAmount = MoneyItem.getTotalMoneyNearby(event.getItem().getLocation(), PICKUP_RADIUS);

            MoneyItem.removeNearbyMoneyItems(event.getItem().getLocation(), PICKUP_RADIUS);

            collectMoney(player, totalAmount);
        }
    }

    private void collectMoney(Player player, double amount) {
        plugin.getEconomy().depositPlayer(player, amount);

        String message = plugin.getConfigManager().getMessage("collect").replace("%amount%", String.valueOf(amount));
        plugin.getMessageUtils().sendActionBar(player, message);

        plugin.getMessageUtils().showBossBar(player, amount);
    }
}