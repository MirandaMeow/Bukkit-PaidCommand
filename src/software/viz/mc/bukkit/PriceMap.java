package software.viz.mc.bukkit;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceMap extends HashMap<Pattern, Double> {
    public double getCost(String command) {
        for (Pattern pattern : this.keySet()) {
            Matcher matcher = pattern.matcher(command);
            if (matcher.find())
                return this.get(pattern);
        }

        return 0;
    }
}
