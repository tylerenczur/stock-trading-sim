package tradingSim.model;

import java.util.Objects;
import tradingSim.exception.InvalidPriceException;
import tradingSim.service.PriceFactory;

public final class Price implements Comparable<Price> {

    private final int price;
    private final String nullMessage = "Price object is null.";

    public Price(int price) {
        this.price = price;
    }

    public boolean isNegative() {
       return this.price < 0;
    }

    public Price add(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("add: " + nullMessage);
        }
        return PriceFactory.makePrice(this.price + p.price);
    }

    public Price subtract(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("subtract: " + nullMessage);
        }
        return PriceFactory.makePrice(this.price - p.price);
    }

    public Price multiply(int n) {
        return PriceFactory.makePrice(this.price * n);
    }

    public boolean greaterOrEqual(Price p) throws InvalidPriceException {
        //Is reusing the function bad practice here?
        return !this.lessThan(p);
    }

    public boolean lessOrEqual(Price p) throws InvalidPriceException {
        return !this.greaterThan(p);
    }

    public boolean greaterThan (Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("greaterThan: " + nullMessage);
        }
        return this.price > p.price;
    }

    public boolean lessThan (Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("lessThan: " + nullMessage);
        }
        return this.price < p.price;
    }

    @Override
    public int compareTo(Price that) {
        if (that == null) {
            return this.price;
        }
        return this.price - that.price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Price price1 = (Price) o;
        return price == price1.price;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.price);
    }

   @Override
   public String toString() {
        return String.format("$%,.2f", this.price/100.00);
   }
}
