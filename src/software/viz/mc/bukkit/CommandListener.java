package software.viz.mc.bukkit;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class CommandListener implements Listener {
    final private PaidCommand PLUGIN;

    public CommandListener(PaidCommand plugin) {
        PLUGIN = plugin;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // TODO: only charge when command runs successfully
        Player player = event.getPlayer();
        if (player == null || player.hasPermission("paidcommand.free"))
            return;

        String command = event.getMessage();
        if (!command.startsWith("/"))
            return;

        PriceEntry priceEntry = PLUGIN.priceList.pricing(command);
        if (priceEntry == null)
            return;

        double cost = priceEntry.getPrice();
        double balance = PLUGIN.economy.getBalance(player);

        Map<String, String> variables = new LinkedHashMap<String, String>();
        variables.put("cost", PLUGIN.economy.format(cost));
        variables.put("balance", PLUGIN.economy.format(balance));

        if (cost > balance) {
            String message = priceEntry.formatMessage(false, variables);
            if (message != null) {
                player.sendMessage(ChatColor.RED + message);
            }
            event.setCancelled(true);

            return;
        }

        EconomyResponse result = PLUGIN.economy.withdrawPlayer(player, cost);
        if (result.transactionSuccess()) {
            balance = balance - cost;
            variables.put("balance", PLUGIN.economy.format(balance));

            String message = priceEntry.formatMessage(true, variables);
            if (message != null) {
                player.sendMessage(ChatColor.GREEN + message);
            }
        }
    }
}
