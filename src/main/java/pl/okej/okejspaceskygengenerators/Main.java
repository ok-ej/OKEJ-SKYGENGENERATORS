package pl.okej.okejspaceskygengenerators;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.okej.okejspaceskygengenerators.commands.SkygenCommands;
import pl.okej.okejspaceskygengenerators.config.ConfigManager;
import pl.okej.okejspaceskygengenerators.generators.GeneratorManager;
import pl.okej.okejspaceskygengenerators.listeners.MoneyPickupListener;
import pl.okej.okejspaceskygengenerators.listeners.GenBoostListener;
import pl.okej.okejspaceskygengenerators.genboost.GenBoostManager;
import pl.okej.okejspaceskygengenerators.utils.MessageUtils;

public final class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private GeneratorManager generatorManager;
    private MessageUtils messageUtils;
    private Economy economy;
    private GenBoostManager genBoostManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadConfig();

        if (!setupEconomy()) {
            getLogger().severe("");
            getLogger().severe(" PLUGIN OKEJSPACE-SKYGENGENERATORS");
            getLogger().severe(" Plugin nie znalazł pluginu Vault lub Essentials");
            getLogger().severe(" Pobierz go tutaj:");
            getLogger().severe(" https://www.spigotmc.org/resources/vault.34315/");
            getLogger().severe("");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        messageUtils = new MessageUtils(this);
        genBoostManager = new GenBoostManager(this);

        generatorManager = new GeneratorManager(this);
        generatorManager.loadGenerators();
        generatorManager.startGenerators();

        getServer().getPluginManager().registerEvents(new MoneyPickupListener(this), this);
        getServer().getPluginManager().registerEvents(new GenBoostListener(this), this);

        getCommand("okejgenerators").setExecutor(new SkygenCommands(this));

        getLogger().info("");
        getLogger().info(" PLUGIN OKEJSPACE-SKYGENGENERATORS");
        getLogger().info(" Plugin został uruchomiony!");
        getLogger().info(" Discord: https://dc.okej.space");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        if (generatorManager != null) {
            generatorManager.stopGenerators();
        }

        if (genBoostManager != null && genBoostManager.isActive()) {
            genBoostManager.stopGenBoost();
        }

        getLogger().info("");
        getLogger().info(" PLUGIN OKEJSPACE-SKYGENGENERATORS");
        getLogger().info(" Plugin został zatrzymany!");
        getLogger().info(" Discord: https://dc.okej.space");
        getLogger().info("");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public static Main getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public MessageUtils getMessageUtils() {
        return messageUtils;
    }

    public Economy getEconomy() {
        return economy;
    }

    public GenBoostManager getGenBoostManager() {
        return genBoostManager;
    }
}