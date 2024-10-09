package uk.gov.dwp.uc.pairtest.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;
import java.util.Map;

class TicketValidatorTest {

    private TicketValidator ticketValidator = new TicketValidator();

    @Test
    @DisplayName("Should Successful process validation for valid tickets")
    public void testValidateTickets_withValidTickets() {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = createTicketTypeCountMap(5, 3, 2);
        boolean isValid = ticketValidator.validateTicketRatio(ticketTypeCountMap);
        Assertions.assertTrue(isValid);
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException when no adult tickets")
    public void testValidateTickets_when_noAdultTickets() {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = createTicketTypeCountMap(0, 3, 2);
        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRatio(ticketTypeCountMap), "InvalidPurchaseException was expected");
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException when infants tickets are more than adult tickets")
    public void testValidateTickets_when_infantTicketsAreMoreThanAdults() {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = createTicketTypeCountMap(5, 3, 6);
        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRatio(ticketTypeCountMap), "InvalidPurchaseException was expected");
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException when total tickets (excluding infants) are more than max allowed")
    public void testValidateTickets_when_moreTicketsThanMaxAllowed() {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = createTicketTypeCountMap(15, 15, 6);
        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRatio(ticketTypeCountMap), "InvalidPurchaseException was expected");
    }

    @Test
    @DisplayName("Should successfully process validate ticket request")
    public void testValidateTicketRequests() {

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5)
        };

        boolean isValidRequest = ticketValidator.validateTicketRequests(ticketTypeRequests);
        Assertions.assertTrue(isValidRequest);
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException for request having null Type")
    public void testValidateTicketRequests_when_typeIsNull() {

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 5),
                new TicketTypeRequest(null, 5)
        };

        InvalidPurchaseException exception = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRequests(ticketTypeRequests), "Expecting InvalidPurchaseException");

    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException for request with negative ticket numbers")
    public void testValidateTicketRequests_when_ticketValueIsGivenInMinus() {

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, -5),
        };

        InvalidPurchaseException exception = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRequests(ticketTypeRequests), "Expecting InvalidPurchaseException");

    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException for empty or NULL request")
    public void testValidateTicketRequests_when_NoTickets() {

        TicketTypeRequest[] ticketTypeRequests = {};
        InvalidPurchaseException exception = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketValidator.validateTicketRequests(ticketTypeRequests), "Expecting InvalidPurchaseException");
    }

    private static Map<TicketTypeRequest.Type, Integer> createTicketTypeCountMap(Integer adultTickets, Integer childTickets, Integer infantTickets) {
        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = new HashMap<>();
        ticketTypeCountMap.put(TicketTypeRequest.Type.ADULT, adultTickets);
        ticketTypeCountMap.put(TicketTypeRequest.Type.CHILD, childTickets);
        ticketTypeCountMap.put(TicketTypeRequest.Type.INFANT, infantTickets);
        return ticketTypeCountMap;
    }

}