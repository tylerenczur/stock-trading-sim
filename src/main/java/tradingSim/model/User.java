package tradingSim.model;

import java.util.HashMap;
import tradingSim.dto.TradableDTO;
import tradingSim.exception.*;

public class User implements CurrentMarketObserver{
    private String userId;
    private final HashMap<String, TradableDTO> tradables = new HashMap<>();
    private final HashMap<String, CurrentMarketSide[]> currentMarkets = new HashMap<>();

    public User(String userId) throws DataValidationException {
        setUserId(userId);
    }

    private void setUserId(String userId) throws DataValidationException {
        if (userId == null || !userId.matches("[a-zA-Z]{3}")) {
            throw new DataValidationException("Invalid userId code");
        }
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void updateTradable(TradableDTO o) {
        if (o != null) {
            tradables.put(o.tradableId(), o);
        }
    }

    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide,
                                    CurrentMarketSide sellSide) {
        if (symbol == null || buySide == null || sellSide == null) {
            throw new DataValidationException("Null symbol or marketSide");
        }
        currentMarkets.put(symbol, new CurrentMarketSide[]{buySide, sellSide});
    }

    public String getCurrentMarkets() {
        StringBuilder res = new StringBuilder();
        for (String s : currentMarkets.keySet()) {
            CurrentMarketSide[] temp = currentMarkets.get(s);
            res.append(String.format("%s   %s - %s\n", s, temp[0], temp[1]));
        }
        return res.toString();
    }

    @Override
    public String toString() {
        StringBuilder s =
                new StringBuilder(String.format("User Id: %s\n", userId));
        for (TradableDTO item : tradables.values()) {
            s.append(String.format("Product: %s, Price: %s, OriginalVol: %d," +
                    " RemainingVol: %d, CancelledVol: %d, FilledVol: %d, " +
                    "User: %s, Side: %s, Id: %s\n", item.product(),
                    item.price(), item.originalVolume(),
                    item.remainingVolume(), item.cancelledVolume(),
                    item.filledVolume(), item.user(), item.side(), item.tradableId()));
        }
        return s.toString();
    }
}
