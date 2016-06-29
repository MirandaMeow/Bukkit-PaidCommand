package software.viz.mc.bukkit;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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

        double cost = PLUGIN.priceMap.getCost(command);
        if (cost == 0d)
            return;

        double balance = PLUGIN.economy.getBalance(player);

        if (cost > balance) {
            String message = PLUGIN.getConfig().getString("insufficientFundsMessage");

            if (message != null && !message.equals("")) {
                message = message.replace("${cost}", PLUGIN.economy.format(cost))
                        .replace("${balance}", PLUGIN.economy.format(balance));
                player.sendMessage(ChatColor.RED + message);
            }
            event.setCancelled(true);

            return;
        }

        EconomyResponse result = PLUGIN.economy.withdrawPlayer(player, cost);
        if (result.transactionSuccess()) {
            String message = PLUGIN.getConfig().getString("accountDeductedMessage");

            if (message != null && !message.equals("")) {
                message = message.replace("${cost}", PLUGIN.economy.format(cost))
                        .replace("${balance}", PLUGIN.economy.format(balance - cost));
                player.sendMessage(ChatColor.GREEN + message);
            }
        }
    }
}
