package software.viz.mc.bukkit;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceEntry {
    private final Pattern pattern;
    private final double price;
    private final String failure;
    private final String success;

    public PriceEntry(Pattern pattern, double price, String success, String failure) {
        this.pattern = pattern;
        this.price = price;
        this.success = success;
        this.failure = failure;
    }

    public double bid(String command) {
        Matcher matcher = pattern.matcher(command);
        if (matcher.find())
            return price;
        return 0d;
    }

    public double getPrice() {
        return price;
    }

    public String formatMessage(boolean successful, Map<String, String> variables) {
        String message = successful ? this.success : this.failure;
        if (message == null || message.equals(""))
            return null;

        for (String key : variables.keySet()) {
            String value = variables.get(key);
            if (value == null)
                value = "";
            message = message.replace("${" + key + "}", value);
        }

        return message;
    }
}
