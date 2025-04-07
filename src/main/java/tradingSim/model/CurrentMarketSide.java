package tradingSim.model;

import tradingSim.exception.*;


public class CurrentMarketSide {
    private Price price;
    private int volume;

    public CurrentMarketSide(Price price, int volume) throws DataValidationException, InvalidPriceException{
        setPrice(price);
        setVolume(volume);
    }

    private void setPrice(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price is null");
        }
        this.price = price;
    }

    private void setVolume(int volume) throws DataValidationException {
        if (volume < 0 || volume >= 10000) {
            throw new DataValidationException("volume is invalid");
        }
        this.volume = volume;
    }

    @Override
    public String toString() {
        return String.format("%sx%d", price, volume);
    }
}
