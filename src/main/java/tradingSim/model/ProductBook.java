package tradingSim.model;

import tradingSim.exception.*;
import tradingSim.service.CurrentMarketTracker;
import tradingSim.service.PriceFactory;
import tradingSim.dto.TradableDTO;
import static tradingSim.model.BookSide.*;


public class ProductBook {
    private String product;
    private final ProductBookSide buySide, sellSide;

    public ProductBook(String product) throws DataValidationException {
        setProduct(product);
        buySide = new ProductBookSide(BUY);
        sellSide = new ProductBookSide(BookSide.SELL);
    }

    private void setProduct(String product) throws DataValidationException {
        if (product == null || !product.matches("[a-zA-Z0-9\\.]{1,5}")) {
            throw new DataValidationException("Invalid product");
        }
        this.product = product;
    }

    public TradableDTO add(Tradable t) throws InvalidTradableException, InvalidPriceException {
        TradableDTO tempDTO;
        if (t == null) {
            throw new InvalidTradableException("Cannot add null tradable");
        }
        if (t.getSide() == BUY) {
            tempDTO = buySide.add(t);
        }
        else {
            tempDTO = sellSide.add(t);
        }
        //System.out.println("**ADD: " + t);
        tryTrade();
        updateMarket();
        return tempDTO;
    }

    public TradableDTO[] add(Quote qte) throws InvalidTradableException, InvalidPriceException {
        if (qte == null) {
            throw new InvalidTradableException("Cannot add null quote");
        }
        TradableDTO[] result = new TradableDTO[2];
        removeQuotesForUser(qte.getUser());
        QuoteSide qBuySide = qte.getQuoteSide(BUY);
        QuoteSide qSellSide = qte.getQuoteSide(BookSide.SELL);
        result[0] = buySide.add(qBuySide);
        result[1] = sellSide.add(qSellSide);
        //System.out.println("**ADD: " + qBuySide);
        //System.out.println("**ADD: " + qSellSide);
        tryTrade();
        return result;
    }

    public TradableDTO cancel(BookSide side, String orderId)
            throws DataValidationException, InvalidPriceException {
        if (side == null) {
            throw new DataValidationException("Cannot cancel null side");
        }
        if (orderId == null) {
            throw new DataValidationException(String.format("Failed to cancel %s order", side));
        }
        TradableDTO res = side == BUY ? buySide.cancel(orderId) :
                sellSide.cancel(orderId);
        updateMarket();
        return res;
    }

    public TradableDTO[] removeQuotesForUser(String userName)
            throws DataValidationException, InvalidPriceException {
        if (userName == null) {
            throw new DataValidationException("Failed to cancel null quote");
        }
        TradableDTO[] result = new TradableDTO[2];
        result[0] = buySide.removeQuotesForUser(userName);
        result[1] = sellSide.removeQuotesForUser(userName);
        updateMarket();
        return result;
    }

    public void tryTrade() throws InvalidPriceException {
        Price buyTop = buySide.topOfBookPrice();
        Price sellTop = sellSide.topOfBookPrice();
        if (buyTop == null || sellTop == null) {
            return;
        }
        int totalToTrade = Math.max(buySide.topOfBookVolume(), sellSide.topOfBookVolume());
        while (totalToTrade > 0) {
            buyTop = buySide.topOfBookPrice();
            sellTop = sellSide.topOfBookPrice();
            if (buyTop == null || sellTop == null || 0 < sellTop.compareTo(buyTop)) {
                return;
            }
            int toTrade = Math.min(buySide.topOfBookVolume(), sellSide.topOfBookVolume());
            buySide.tradeOut(buyTop, toTrade);
            sellSide.tradeOut(buyTop, toTrade);
            totalToTrade -= toTrade;
        }
    }

    private void updateMarket() throws InvalidPriceException {
        CurrentMarketTracker cmt = CurrentMarketTracker.getInstance();
        Price buyTop = buySide.topOfBookPrice();
        Price sellTop = sellSide.topOfBookPrice();
        buyTop = buyTop == null ? PriceFactory.makePrice(0) : buyTop;
        sellTop = sellTop == null ? PriceFactory.makePrice(0) : sellTop;
        cmt.updateMarket(product, buyTop, buySide.topOfBookVolume(), sellTop, sellSide.topOfBookVolume());
    }

    public String getTopOfBookString(BookSide side) {
        ProductBookSide bookSide = side == BUY ? buySide : sellSide;
        Price price = bookSide.topOfBookPrice();
        return String.format("Top of %s book: %s x %d", side,
                price == null ? "$0.00" : price, bookSide.topOfBookVolume());
    }

    @Override
    public String toString() {
        String wrapper = "--------------------------------------------\n";
        return String.format("%sProduct Book: %s\n%s%s%s", wrapper, product,
                buySide, sellSide, wrapper);
    }

}
