package tradingSim.service;

import tradingSim.model.CurrentMarketObserver;
import tradingSim.exception.DataValidationException;
import tradingSim.model.CurrentMarketSide;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentMarketPublisher {

    private static CurrentMarketPublisher instance;
    private final HashMap<String, ArrayList<CurrentMarketObserver>> filters = new HashMap<>();

    private CurrentMarketPublisher(){}

    public static CurrentMarketPublisher getInstance() {
        if (instance == null) {
            instance = new CurrentMarketPublisher();
        }
        return instance;
    }

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo)
            throws DataValidationException {
        if (symbol == null || cmo == null) {
            throw new DataValidationException("NULL cmo or symbol");
        }
        if (!filters.containsKey(symbol)) {
            filters.put(symbol, new ArrayList<>());
        }
        filters.get(symbol).add(cmo);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo)
            throws DataValidationException {
        if (symbol == null || cmo == null) {
            throw new DataValidationException("NULL cmo or symbol");
        }
        if (filters.containsKey(symbol) && filters.get(symbol).contains(cmo)) {
            filters.get(symbol).remove(cmo);
        }
    }

    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        if (symbol == null || buySide == null || sellSide == null) {
            throw new DataValidationException("NULL marketSide or symbol");
        }
        if (filters.containsKey(symbol)) {
           for (CurrentMarketObserver cmo : filters.get(symbol)) {
              cmo.updateCurrentMarket(symbol, buySide, sellSide);
           }
        }
    }


}
