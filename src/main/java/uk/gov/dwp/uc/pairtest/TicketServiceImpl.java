package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketPrice;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.AccountValidator;
import uk.gov.dwp.uc.pairtest.validation.TicketValidator;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {

    private AccountValidator accountValidator;
    private TicketValidator ticketValidator;

    private SeatReservationService seatReservationService;
    private TicketPaymentService ticketPaymentService;

    public TicketServiceImpl(AccountValidator accountValidator, TicketValidator ticketValidator,
                             SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService) {
        this.accountValidator = accountValidator;
        this.ticketValidator = ticketValidator;
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
    }

    /**
     * Should only have private methods other than the one below.
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        accountValidator.validateAccount(accountId);
        ticketValidator.validateTicketRequests(ticketTypeRequests);

        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = createTicketTypeCountMap(ticketTypeRequests);
        ticketValidator.validateTicketRatio(ticketTypeCountMap);

        doSeatReservation(accountId, ticketTypeCountMap);
        doTicketPayment(accountId, ticketTypeCountMap);
    }

    /**
     * Reserve Seat allocation
     *
     * @param accountId
     * @param ticketTypeCountMap
     */
    private void doSeatReservation(Long accountId, Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap) {

        int reserveSeatCount = ticketTypeCountMap.get(TicketTypeRequest.Type.ADULT) + ticketTypeCountMap.get(TicketTypeRequest.Type.CHILD);
        seatReservationService.reserveSeat(accountId, reserveSeatCount);
    }

    /**
     * Calculate and process payment for valid tickets
     *
     * @param accountId
     * @param ticketTypeCountMap
     */
    private void doTicketPayment(Long accountId, Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap) {

        int adultTicketsAmount = ticketTypeCountMap.get(TicketTypeRequest.Type.ADULT) * TicketPrice.ADULT.getPrice();
        int childTicketsAmount = ticketTypeCountMap.get(TicketTypeRequest.Type.CHILD) * TicketPrice.CHILD.getPrice();
        int totalAmount = adultTicketsAmount + childTicketsAmount;

        ticketPaymentService.makePayment(accountId, totalAmount);
    }

    /**
     * Generate Map of Type and ticket count for each type
     *
     * @param ticketTypeRequests
     * @return
     */
    private Map<TicketTypeRequest.Type, Integer> createTicketTypeCountMap(TicketTypeRequest[] ticketTypeRequests) {

        Map<TicketTypeRequest.Type, Integer> ticketTypeCountMap = Arrays.stream(ticketTypeRequests)
                .collect(Collectors
                        .groupingBy(TicketTypeRequest::getTicketType,
                                Collectors.summingInt(TicketTypeRequest::getNoOfTickets)));

        for (TicketTypeRequest.Type type : TicketTypeRequest.Type.values()) {
            ticketTypeCountMap.put(type, ticketTypeCountMap.getOrDefault(type, 0));
        }

        return ticketTypeCountMap;
    }

}
