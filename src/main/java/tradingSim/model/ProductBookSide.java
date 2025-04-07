package tradingSim.model;

import tradingSim.dto.TradableDTO;
import tradingSim.exception.*;
import tradingSim.service.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeMap;

public class ProductBookSide {
    private BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side) throws DataValidationException{
        setSide(side);
        this.bookEntries = new TreeMap<>(side == BookSide.BUY ? Collections.reverseOrder() : Comparator.naturalOrder());
    }

    private void setSide(BookSide side) throws DataValidationException {
        if (side == null) {
            throw new DataValidationException("Side is null");
        }
        this.side = side;
    }

    public TradableDTO add(Tradable o) throws InvalidTradableException{
        if (o == null) {
            throw new InvalidTradableException("Cannot add null tradable");
        }
        Price key = o.getPrice();
        if (!bookEntries.containsKey(key)) {
            bookEntries.put(key, new ArrayList<>());
        }
        bookEntries.get(key).add(o);
        TradableDTO res = o.makeTradableDTO();
        UserManager.getInstance().updateTradable(o.getUser(), res);
        return res;
    }

    //Assuming empty entry removal occurs still due to the call of cancel
    //Could eventually improve performance by removing additional search
    public TradableDTO removeQuotesForUser(String userName) throws DataValidationException {
        if (userName == null) {
            throw new DataValidationException("Cannot remove null quote");
        }
        for (Price key : bookEntries.keySet()) {
            for (Tradable t : bookEntries.get(key)) {
                if(t.getUser().equals(userName)) {
                    //DTO updated by cancel
                    return cancel(t.getId());
                }
            }
        }
        return null;
    }

    public TradableDTO cancel(String tradableId) throws DataValidationException {
        if (tradableId == null) {
            throw new DataValidationException("Cannot remove null quote");
        }
        for (Price key : bookEntries.keySet()) {
            for (Tradable t : bookEntries.get(key)) {
                if(t.getId().equals(tradableId)) {
                    //System.out.println("**CANCEL: " + t);
                    bookEntries.get(key).remove(t);
                    t.setCancelledVolume(t.getRemainingVolume() + t.getCancelledVolume());
                    t.setRemainingVolume(0);
                    if (bookEntries.get(key).isEmpty()) {
                        bookEntries.remove(key);
                    }
                    TradableDTO res = t.makeTradableDTO();
                    UserManager.getInstance().updateTradable(t.getUser(), res);
                    return res;
                }
            }
        }
        return null;
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        }
        return bookEntries.firstKey();
    }

    public int topOfBookVolume() {
        if (bookEntries.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (Tradable t : bookEntries.get(bookEntries.firstKey())) {
            total += t.getRemainingVolume();
        }
        return total;
    }

    public void tradeOut(Price price, int volToTrade) throws InvalidPriceException {
        // I know this isn't needed, and is probably pedantic, but id rather have it jic.
        if (price == null) {
            throw new InvalidPriceException("Cannot trade out null price.");
        }
        Price topPrice = topOfBookPrice();
        if (topPrice == null || 0 < topPrice.compareTo(price)) {
            return;
        }
        ArrayList<Tradable> entriesAtPrice = bookEntries.get(topPrice);
        int totalVolAtPrice = topOfBookVolume();
        if (volToTrade >= totalVolAtPrice) {
            for (Tradable t : entriesAtPrice) {
                int rv = t.getRemainingVolume();
                t.setFilledVolume(t.getOriginalVolume());
                t.setRemainingVolume(0);
                System.out.printf("FULL FILL: (%s %d) %s\n", side, rv, t);
                UserManager.getInstance().updateTradable(t.getUser(), t.makeTradableDTO());
            }
            bookEntries.remove(topPrice);
        }
        else {
            int remainder = volToTrade;
            for (Tradable t : entriesAtPrice) {
                double ratio = (double)t.getRemainingVolume()/totalVolAtPrice;
                int toTrade = (int)Math.ceil(ratio * volToTrade);
                toTrade = Math.min(remainder, toTrade);
                t.setFilledVolume(t.getFilledVolume() + toTrade);
                t.setRemainingVolume(t.getRemainingVolume() - toTrade);
                System.out.printf("PARTIAL FILL: (%s %d) %s\n", side, toTrade, t);
                remainder -= toTrade;
                UserManager.getInstance().updateTradable(t.getUser(), t.makeTradableDTO());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder entries = new StringBuilder();
        if (bookEntries.isEmpty()) {
            entries.append("    <Empty>\n");
        }
        else {
            for (Price key : bookEntries.keySet()) {
                entries.append(String.format("    %s:\n", key));
                for (Tradable t : bookEntries.get(key)) {
                    entries.append(String.format("        %s\n", t));
                }
            }
        }
        return String.format("Side: %s\n%s", side, entries);
    }
}
