package tradingSim.model;

import tradingSim.exception.*;

public class Quote {
    private String user, product;
    private final QuoteSide buySide, sellSide;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName)
            throws InvalidPriceException, DataValidationException {
        setUser(userName);
        setProduct(symbol);
        buySide = new QuoteSide(user, product, BookSide.BUY, buyPrice, buyVolume);
        sellSide = new QuoteSide(user, product, BookSide.SELL, sellPrice, sellVolume);
    }

    public QuoteSide getQuoteSide(BookSide sideIn) throws DataValidationException {
        if (sideIn == null) {
            throw new DataValidationException("Side is null.");
        }
        if (sideIn == BookSide.BUY) {
            return buySide;
        }
        return sellSide;
    }

    public String getSymbol() {
        return product;
    }

    public String getUser() {
        return user;
    }

    private void setUser(String user) throws DataValidationException {
        if (user == null || !user.matches("[a-zA-Z]{3}")) {
            throw new DataValidationException("Invalid user code");
        }
        this.user = user;
    }

    private void setProduct(String product) throws DataValidationException {
        if (product == null || !product.matches("[\\.a-zA-Z0-9]{1,5}")) {
            throw new DataValidationException("Invalid product");
        }
        this.product = product;
    }
}
