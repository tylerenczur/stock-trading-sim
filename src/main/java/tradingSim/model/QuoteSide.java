package tradingSim.model;

import tradingSim.dto.TradableDTO;
import tradingSim.exception.*;

public class QuoteSide implements Tradable{
    private String user, product, id;
    private Price price;
    private BookSide side;
    private int originalVolume;
    private int remainingVolume, cancelledVolume, filledVolume;

    public QuoteSide(String user, String product, BookSide side,
                     Price price, int originalVolume) throws InvalidPriceException, DataValidationException {
        setUser(user);
        setProduct(product);
        setPrice(price);
        setSide(side);
        setOriginalVolume(originalVolume);
        setRemainingVolume(originalVolume);
        setCancelledVolume(0);
        setFilledVolume(0);
        setId(this.user, this.product, this.price);
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

    private void setPrice(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price is null");
        }
        this.price = price;
    }

    private void setSide(BookSide side) throws DataValidationException {
        if (side == null) {
            throw new DataValidationException("Side is null");
        }
        this.side = side;
    }

    private void setOriginalVolume(int originalVolume) throws DataValidationException {
        if (originalVolume <= 0 || originalVolume >= 10000) {
            throw new DataValidationException("originalVolume is invalid");
        }
        this.originalVolume = originalVolume;
    }

    private void setId(String user, String product, Price price) {
        this.id = String.format("%s%s%s%d", user, product, price.toString(), System.nanoTime());
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getRemainingVolume() {
        return remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol) {
        cancelledVolume = newVol;
    }

    @Override
    public int getCancelledVolume() {
        return cancelledVolume;
    }

    @Override
    public void setRemainingVolume(int newVol) {
        remainingVolume = newVol;
    }

    @Override
    public TradableDTO makeTradableDTO() {
        return new TradableDTO(this);
    }

    @Override
    public Price getPrice() {
        return price;
    }

    @Override
    public void setFilledVolume(int newVol) {
        filledVolume = newVol;
    }

    @Override
    public int getFilledVolume() {
        return filledVolume;
    }

    @Override
    public BookSide getSide() {
        return side;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public int getOriginalVolume() {
        return originalVolume;
    }

    @Override
    public String toString() {
        return String.format("%s %s side quote for %s:%s, Orig Vol: %d, " +
                        "Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side.toString(), product, price.toString(), originalVolume,
                remainingVolume, filledVolume, cancelledVolume, id);
    }

}
