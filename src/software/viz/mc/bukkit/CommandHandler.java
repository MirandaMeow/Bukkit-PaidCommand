package software.viz.mc.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
    final private PaidCommand PLUGIN;

    public CommandHandler(PaidCommand plugin) {
        PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp() && !player.hasPermission("paidcommand.reload")) {
                sender.sendMessage(ChatColor.DARK_RED + "You don't have enough permission!");
                return true;
            }
        }

        if (args.length == 0) {
            showVersion(sender);
            return true;
        }

        if (args.length == 1 && args[0].equals("reload")) {
            PLUGIN.reload();
            sender.sendMessage("Configuration reloaded!");
            return true;
        }

        return false;
    }

    private void showVersion(CommandSender sender) {
        sender.sendMessage(String.format("[%s] by %s, Version %s",
                PLUGIN.getDescription().getName(),
                PLUGIN.getDescription().getAuthors().get(0),
                PLUGIN.getDescription().getVersion()));
    }
}
