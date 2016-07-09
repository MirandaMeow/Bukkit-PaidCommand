package software.viz.mc.bukkit;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class PaidCommand extends JavaPlugin {
    private Logger log;
    public Economy economy;
    public PriceList priceList;

    @Override
    public void onLoad() {
        log = this.getServer().getLogger();
        priceList = new PriceList();

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

        List<Map<?, ?>> prices = this.getConfig().getMapList("prices");
        priceList.clear();

        for (Map<?, ?> entry : prices) {
            String regex = null;
            Pattern pattern;
            double price;
            String success = this.getConfig().getString("default-success", null);
            String failure = this.getConfig().getString("default-failure", null);

            try {
                regex = String.valueOf(entry.get("regex"));
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                price = Double.parseDouble(String.valueOf(entry.get("price")));
                if (entry.containsKey("success")) {
                    success = String.valueOf(entry.get("success"));
                }
                if (entry.containsKey("failure")) {
                    failure = String.valueOf(entry.get("failure"));
                }
            } catch (Exception ignored) {
                if (regex == null) {
                    log.warning("Invalid price entry found!");
                } else {
                    log.warning("Invalid price entry for command pattern " + regex);
                }
                continue;
            }

            PriceEntry priceEntry = new PriceEntry(pattern, price, success, failure);
            priceList.add(priceEntry);
        }
    }
}
