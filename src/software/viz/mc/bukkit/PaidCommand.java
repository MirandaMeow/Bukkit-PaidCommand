package software.viz.mc.bukkit;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PaidCommand extends JavaPlugin {
    private Logger log;
    public Economy economy;
    public PriceMap priceMap;

    @Override
    public void onLoad() {
        log = this.getServer().getLogger();
        priceMap = new PriceMap();

        updateConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        setupEconomy();
        reload();

        this.getServer().getPluginManager().registerEvents(new CommandListener(this), this);
        this.getCommand("paidcommand").setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        // TODO really need this?
    }

    private boolean setupEconomy() {
        if (!foundVault())
            return false;

        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            log.info("Economy Plugin found! " + economy.getName() + " hooked!");

            return true;
        }

        return false;
    }

    private boolean foundVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            log.warning("Vault not found. Support Disabled!");

            return false;
        }
        return true;
    }

    private void updateConfig() {
        this.saveDefaultConfig();
    }

    public void reload() {
        this.reloadConfig();

        ConfigurationSection prices = this.getConfig().getConfigurationSection("prices");
        priceMap.clear();

        for (String regex : prices.getKeys(false)) {
            double price;
            Pattern pattern;

            try {
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                price = prices.getDouble(regex);
            } catch (Exception ignored) {
                log.warning("Invalid price for command pattern " + regex);
                continue;
            }

            priceMap.put(pattern, price);
        }
    }
}
