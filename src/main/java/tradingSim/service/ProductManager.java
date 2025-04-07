package tradingSim.service;

import java.util.ArrayList;
import java.util.HashMap;

import tradingSim.dto.TradableDTO;
import tradingSim.model.BookSide;
import tradingSim.model.ProductBook;
import tradingSim.model.Quote;
import tradingSim.exception.*;
import tradingSim.model.Tradable;

public class ProductManager {
    private static ProductManager instance;
    private final HashMap<String, ProductBook> books = new HashMap<>();

    private ProductManager(){}

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }
    public void addProduct(String symbol) throws DataValidationException {
        if (symbol == null) {
            throw new DataValidationException("symbol is null");
        }
        if (!books.containsKey(symbol)) {
            books.put(symbol, new ProductBook(symbol));
        }
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (symbol == null || !books.containsKey(symbol)) {
            throw new DataValidationException("Symbol not found");
        }
        return books.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException {
        int n = books.size();
        if (n == 0) {
            throw new DataValidationException("No products to select");
        }
        ArrayList<String> list = new ArrayList<>(books.keySet());
        return list.get(Math.min((int)(Math.random() * n), n-1));
    }

    public TradableDTO addTradable(Tradable o)
            throws InvalidTradableException, InvalidPriceException, DataValidationException {
        if (o == null) {
            throw new DataValidationException("null tradable object");
        }
        if (!books.containsKey(o.getProduct())) {
            throw new DataValidationException("attempted to add tradable " +
                    "where book didn't exist.");
        }
        books.get(o.getProduct()).add(o);
        TradableDTO res = o.makeTradableDTO();
        UserManager.getInstance().updateTradable(o.getUser(), res);
        return res;
    }

    public TradableDTO[] addQuote(Quote q)
            throws DataValidationException, InvalidPriceException, InvalidTradableException {
        if (q == null) {
            throw new DataValidationException("null quote");
        }
        if (!books.containsKey(q.getSymbol())) {
            throw new DataValidationException("attempted to add quote where " +
                    "book didn't exist.");
        }
        books.get(q.getSymbol()).removeQuotesForUser(q.getUser());
        return new TradableDTO[] {
                addTradable(q.getQuoteSide(BookSide.BUY)),
                addTradable(q.getQuoteSide(BookSide.SELL))
        };
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException, InvalidPriceException {
        if (o == null) {
            throw new DataValidationException("null tradable object");
        }
        if (!books.containsKey(o.product())) {
            throw new DataValidationException("attempted to cancel " +
                    "non-existing tradable");
        }
        TradableDTO res = books.get(o.product()).cancel(o.side(),
                o.tradableId());
        if (res == null) {
            System.out.println("Failed to cancel tradable.");
        }
        return res;
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, InvalidPriceException {
        if (symbol == null || user == null) {
            throw new DataValidationException("null symbol or user");
        }
        if (!books.containsKey(symbol)) {
            throw new DataValidationException("attempted to cancel non-existing tradable");
        }
        TradableDTO[] res = books.get(symbol).removeQuotesForUser(user);
//        if (res == null || res[0] == null || res[1] == null) {
//            System.out.println("Partial or complete failure to cancel.");
//        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (ProductBook book : books.values()) {
            s.append(String.format("%s\n", book));
        }
        return s.toString();
    }

}
