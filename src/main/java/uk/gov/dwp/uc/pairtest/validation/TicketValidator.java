package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.Map;

public class TicketValidator {

    private static int MAX_ALLOWED_TICKETS = 25;

    public boolean validateTicketRequests(TicketTypeRequest[] ticketTypeRequests) throws InvalidPurchaseException {

        if(ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("At least one ticket must be purchased");
        }

        if(Arrays.stream(ticketTypeRequests).anyMatch(t -> t.getTicketType() == null)) {
            throw new InvalidPurchaseException("The request type cannot be empty");
        }

        if(Arrays.stream(ticketTypeRequests).anyMatch(t -> t.getNoOfTickets() < 0)) {
            throw new InvalidPurchaseException("The number of tickets requested must not be negative");
        }

        return true;
    }

    public boolean validateTicketRatio(Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap) throws InvalidPurchaseException {

        boolean isValid = true;

        int adultTickets = ticketTypeCountMap.get(TicketTypeRequest.Type.ADULT);
        int childTickets = ticketTypeCountMap.get(TicketTypeRequest.Type.CHILD);
        int infantTickets = ticketTypeCountMap.get(TicketTypeRequest.Type.INFANT);

        int totalTickets = adultTickets + childTickets;

        if (totalTickets <= 0 ) {
            throw new InvalidPurchaseException("At least one ticket must be purchased");
        } else if (adultTickets <= 0 ) {
            throw new InvalidPurchaseException("At least one adult ticket must be purchased");
        } else if (totalTickets > MAX_ALLOWED_TICKETS) {
            throw new InvalidPurchaseException(String.format("Cannot purchase more than %s tickets at a time", MAX_ALLOWED_TICKETS));
        } else if (infantTickets > adultTickets) {
            throw new InvalidPurchaseException("Number of infant tickets cannot exceed the number of adult tickets");
        }

        return isValid;
    }

}
