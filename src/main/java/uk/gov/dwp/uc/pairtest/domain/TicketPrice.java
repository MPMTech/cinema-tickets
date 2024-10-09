package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;

/**
 *  Defining price for each type of ticket type - ADULT, CHILD, INFANT
 */
public enum TicketPrice {

    ADULT(Type.ADULT, 25),
    CHILD(Type.CHILD, 15),
    INFANT(Type.INFANT, 0);

    private final Type ticketType;
    private final int ticketTypePrice;

    TicketPrice(Type ticketType, int ticketTypePrice) {
        this.ticketType = ticketType;
        this.ticketTypePrice = ticketTypePrice;
    }

    public int getPrice() {
        return ticketTypePrice;
    }

    public Type getTicketType() {
        return ticketType;
    }

    public int getPriceForType(Type ticketType) {
        int price = 0;
        for(TicketPrice ticketPrice: values()) {
            if(ticketPrice.getTicketType() == ticketType) {
                price =  ticketPrice.getPrice();
                break;
            }
        }
        return price;
    }

}
