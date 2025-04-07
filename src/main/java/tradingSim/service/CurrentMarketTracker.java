package tradingSim.service;

import tradingSim.exception.*;
import tradingSim.model.Price;
import tradingSim.model.CurrentMarketSide;

public class CurrentMarketTracker {

    private static CurrentMarketTracker instance;

    private CurrentMarketTracker() {}

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume,
                             Price sellPrice, int sellVolume) throws InvalidPriceException, DataValidationException {
        Price marketWidth = PriceFactory.makePrice(0);
        CurrentMarketSide buySide, sellSide;
        if (sellPrice == null || buyPrice == null || buyVolume == 0 || sellVolume == 0) {
            marketWidth = PriceFactory.makePrice(0);
        }
        else {
            marketWidth = sellPrice.subtract(buyPrice);
            if (marketWidth.isNegative()) {
                marketWidth = PriceFactory.makePrice(0);
            }
        }

        buySide = new CurrentMarketSide(buyPrice, buyVolume);
        sellSide = new CurrentMarketSide(sellPrice, sellVolume);
        String topBar = "*********** Current Market ***********";
        String bottomBar = "**************************************";
        System.out.printf("%s\n* %s   %sx%d - %sx%d [%s]\n%s\n",
                topBar, symbol, buyPrice, buyVolume, sellPrice,
                sellVolume, marketWidth, bottomBar);
        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }


}
