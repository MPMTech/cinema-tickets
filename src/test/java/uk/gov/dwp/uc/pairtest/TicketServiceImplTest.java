package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketPrice;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validation.AccountValidator;
import uk.gov.dwp.uc.pairtest.validation.TicketValidator;

import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    private Long accountId = 1001L;
    private TicketService ticketService;

    private SeatReservationService seatReservationService;
    private TicketPaymentService ticketPaymentService;

    private AccountValidator accountValidator = new AccountValidator();
    private TicketValidator ticketValidator = new TicketValidator();

    @BeforeEach
    public void setUp() {
        seatReservationService = mock(SeatReservationServiceImpl.class);
        ticketPaymentService = mock(TicketPaymentServiceImpl.class);

        ticketService = new TicketServiceImpl(accountValidator, ticketValidator, seatReservationService, ticketPaymentService);
    }


    @Test
    @DisplayName("Should able to reserve and purchase tickets with valid ticket requests")
    public void testValidPurchaseTickets() {
        verifyPurchaseTicket(5, 3, 2);
        verifyPurchaseTicket(5, 0, 0);
        verifyPurchaseTicket(25, 0, 0);
        verifyPurchaseTicket(1, 0, 0);
        verifyPurchaseTicket(1, 1, 1);
        verifyPurchaseTicket(15, 10, 10);
    }

    @Test
    @DisplayName("Should throws InvalidPurchaseException when total number of seat are more than max allowed(25)")
    public void testInvalidPurchaseTickets_requestTicketMoreThanAllowed() {

        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(accountId,
                        createTicketRequest(Type.ADULT, 15),
                        createTicketRequest(Type.CHILD, 13),
                        createTicketRequest(Type.INFANT, 2)));

        verify(seatReservationService, never()).reserveSeat(any(Integer.class), any(Integer.class));
        verify(ticketPaymentService, never()).makePayment(any(Integer.class), any(Integer.class));
    }

    @Test
    @DisplayName("Should throw InvalidPurchaseException when request has no adult tickets")
    public void testInvalidPurchaseTickets_requestTicketWithoutAnyAdult() {

        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(accountId,
                        createTicketRequest(Type.ADULT, 0),
                        createTicketRequest(Type.CHILD, 13),
                        createTicketRequest(Type.INFANT, 2)));

        verify(seatReservationService, never()).reserveSeat(any(Integer.class), any(Integer.class));
        verify(ticketPaymentService, never()).makePayment(any(Integer.class), any(Integer.class));
    }


    @Test
    @DisplayName("Should throw InvalidPurchaseException when request has null Type")
    public void testInvalidPurchaseTickets_requestTicketWithoutAnyAdult_test() {

        InvalidPurchaseException invalidPurchaseException = Assertions.assertThrowsExactly(InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(accountId,
                        createTicketRequest(null, 10),
                        createTicketRequest(Type.CHILD, 13),
                        createTicketRequest(Type.INFANT, 2)));

        verify(seatReservationService, never()).reserveSeat(any(Integer.class), any(Integer.class));
        verify(ticketPaymentService, never()).makePayment(any(Integer.class), any(Integer.class));
    }

    @Test
    @DisplayName("Should be successfully purchase tickets when adult and child tickets count are more than 25 ignoring infant tickets")
    public void testValidPurchaseTickets_withInfantCount_aboveMaxAllowed() {
        verifyPurchaseTicket(15, 10, 10);
    }

    private void verifyPurchaseTicket(int adultTickets, int childTickets, int infantTickets) {

        int totalSeatToAllocate = adultTickets + childTickets;
        int totalAmount = calculateTotalAmount(adultTickets, childTickets);

        ticketService.purchaseTickets(accountId, createTicketRequest(Type.ADULT, adultTickets),
                createTicketRequest(Type.CHILD, childTickets),
                createTicketRequest(Type.INFANT, infantTickets));

        verify(seatReservationService).reserveSeat(accountId, totalSeatToAllocate);
        verify(ticketPaymentService).makePayment(accountId, totalAmount);

        reset(seatReservationService, ticketPaymentService);
    }

    private TicketTypeRequest createTicketRequest(Type type, int noOfTickets) {
        return new TicketTypeRequest(type, noOfTickets);
    }

    private int calculateTotalAmount(int adultTickets, int childTickets) {
        return (adultTickets * TicketPrice.ADULT.getPrice())
                + (childTickets * TicketPrice.CHILD.getPrice());
    }


}