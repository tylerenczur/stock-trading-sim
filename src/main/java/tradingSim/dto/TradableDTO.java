package tradingSim.dto;

import tradingSim.model.BookSide;
import tradingSim.model.Price;
import tradingSim.model.Tradable;

public record TradableDTO(String user, String product, String tradableId,
                          Price price, BookSide side, int originalVolume,
                          int remainingVolume, int cancelledVolume,
                          int filledVolume) {
    public TradableDTO(Tradable tradable) {
        this(tradable.getUser(), tradable.getProduct(), tradable.getId(),
                tradable.getPrice(), tradable.getSide(),
                tradable.getOriginalVolume(), tradable.getRemainingVolume(),
                tradable.getCancelledVolume(), tradable.getFilledVolume());
    }

}
