package tradingSim.service;

import tradingSim.model.Price;
import tradingSim.exception.InvalidPriceException;

import java.util.HashMap;

public abstract class PriceFactory {

    private static final HashMap<Integer, Price> cache = new HashMap<>();

    public static Price makePrice(int value) {
        if (!cache.containsKey(value)) {
            cache.put(value, new Price(value));
        }
        return cache.get(value);
    }

    //Probably much cleaner way of doing this (try regex)
    public static Price makePrice(String stringValueIn) throws InvalidPriceException {
        if (stringValueIn == null || stringValueIn.isEmpty()) {
            throw new InvalidPriceException("Input string is null or empty");
        }
        String invalidMessage = "Invalid price: " + stringValueIn;
        String s = stringValueIn.replaceAll(",", "");

        if (s.charAt(0) == '$') {
            s = s.substring(1);
        }

        int decimalIndex = s.indexOf('.');
        int n = s.length();
        int factor = 1;

        s = s.replaceFirst("\\.", "");
        if (decimalIndex == n - 1 || decimalIndex == -1) {
            factor = 100;
        } else if (decimalIndex != n - 3) {
            throw new InvalidPriceException(invalidMessage);
        }

        try {
            int value = factor * Integer.parseInt(s);
            return makePrice(value);
        } catch (NumberFormatException e) {
            throw new InvalidPriceException(invalidMessage);
        }

    }

    //attempt to clean up factory with regex stuff, unfinished
//    public static Price makePriceNew(String stringValueIn) throws InvalidPriceException {
//        if (stringValueIn == null || stringValueIn.isEmpty()) {
//            throw new InvalidPriceException("Input string is null or empty");
//        }
//        String invalidMessage = "Invalid price: " + stringValueIn;
//        String s = stringValueIn.replaceAll(",", "");
//        if (!s.matches("\\$?\\d*\\.?(?:\\d{2})?")) {
//            throw new InvalidPriceException(invalidMessage);
//        }
//        try {
//            int value = 1; //placeholder
//            return new Price(value);
//        } catch (NumberFormatException e) {
//            throw new InvalidPriceException(invalidMessage);
//        }
//
//    }
}
