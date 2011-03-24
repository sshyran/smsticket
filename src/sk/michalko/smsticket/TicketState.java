package sk.michalko.smsticket;

public enum TicketState {
	
	TICKET_ORDER_CREATED, // Ticket created localy
	TICKET_ORDER_IN_PROGRESS, // Ticket request sms sent
	TICKET_ORDER_CONFIRMED, // Ticket request delivered
	TICKET_VALID, // Ticket arrived
	TICKET_EXPIRED  // Ticket expired

}
