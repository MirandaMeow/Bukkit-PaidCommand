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
        if (args.length == 0) {
            showVersion(sender);
            return true;
        }

        if (args.length == 1 && args[0].equals("reload")) {
            if (!validatePermission(sender, "paidcommand.reload"))
                return true;

            PLUGIN.reload();
            sender.sendMessage("Configuration reloaded!");
            return true;
        }

        if (args.length == 1 && args[0].equals("list")) {
            if (!validatePermission(sender, "paidcommand.list"))
                return true;

            for (PriceEntry entry : PLUGIN.priceList) {
                sender.sendMessage(entry.getRegex() + ": " + entry.getPrice());
            }

            return true;
        }

        if (args.length >= 1 && args[0].equals("test")) {
            if (!validatePermission(sender, "paidcommand.test"))
                return true;

            if (args.length < 2 || !args[1].startsWith("/")) {
                sender.sendMessage(ChatColor.DARK_RED + "No command to test!");
                return true;
            }

            String testCommand = args[1];
            for (int i = 2; i < args.length; i++) {
                testCommand += " " + args[i];
            }
            PriceEntry priceEntry = PLUGIN.priceList.pricing(testCommand);
            sender.sendMessage(ChatColor.GREEN + "command " + ChatColor.RESET + testCommand + ChatColor.GREEN
                    + (priceEntry == null ? " is free." : " costs " + PLUGIN.economy.format(priceEntry.getPrice()) + "."));

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

    private boolean validatePermission(CommandSender sender, String node) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        if (player.hasPermission(node))
            return true;

        sender.sendMessage(ChatColor.DARK_RED + "You don't have enough permission!");
        return false;
    }
}
