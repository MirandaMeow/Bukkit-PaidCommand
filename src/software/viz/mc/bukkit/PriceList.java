package software.viz.mc.bukkit;

import java.util.LinkedList;

public class PriceList extends LinkedList<PriceEntry> {
    public PriceEntry pricing(String command) {
        double winnerBid = 0d;
        PriceEntry winner = null;
        for (PriceEntry entry : this) {
            double entryBid = entry.bid(command);
            if (entryBid > winnerBid) {
                winnerBid = entryBid;
                winner = entry;
            }
        }

        return winner;
    }
}
