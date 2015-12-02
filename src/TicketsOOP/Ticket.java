package TicketsOOP;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ticket implements Comparable<Ticket>{

    private int priority;
    private String reporter; //Stores person or department who reported issue
    private String description;
    private Date dateReported;
    private Date dateResolved;
    private String resolution;

    private static int staticTicketIDCounter = 1;
    /* STATIC Counter - accessible to all Ticket objects.
     * If any Ticket object modifies this counter, all Ticket objects will have the modified value.
     * Make it private - only Ticket objects should have access. */

    /* The ID for each ticket - instance variable. Each Ticket will have it's own ticketID variable */
    protected int ticketID;

    /* A constructor would be useful */
    public Ticket(String desc, int p, String rep, Date date) {
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.ticketID = staticTicketIDCounter;
        staticTicketIDCounter++;
    }

    //Return a negative number if this ticket should be in front of another ticket in the list – it has a higher priority so should be at the front of the list
    //Return a positive number if this ticket should be behind another ticket in the list
    //And it needs to return 0 if the tickets are equivalent when sorted

    @Override
    public int compareTo(Ticket anotherTicket) {

        if (this.priority < anotherTicket.getPriority() ) {
            return 1;
        } else if (this.priority > anotherTicket.getPriority()) {
            return -1;
        } else {
            return 0;
        }

    }

    /* Setters & Getters */
    public String getDescription() {return description;}
    protected int getPriority() {return priority;}
    public String getReporter() {return reporter;}
    public Date getDateReported() {return dateReported;}
    protected int getTicketID() {return ticketID;}
    public Date getDateResolved() {return dateResolved;}
    public void setDateResolved(Date dateResolved) {this.dateResolved = dateResolved;}
    public String getResolution() {return resolution;}
    public void setResolution(String resolution) {this.resolution = resolution;}
    public void setTicketID(int ticketID) {this.ticketID = ticketID;}
    public static int getStaticTicketIDCounter() {return staticTicketIDCounter;}
    public static void setStaticTicketIDCounter(int staticTicketIDCounter) {Ticket.staticTicketIDCounter = staticTicketIDCounter;}

    /* Called automatically if a Ticket object is an argument to System.out.println */
    public String toString(){
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        String resolutionString = (this.resolution == null) ? "Open Ticket" : this.resolution;
        String dateResolved = (this.dateResolved == null) ? "Open Ticket" : dateFormat.format(this.dateResolved);

        return("ID= " + this.ticketID + ", Issue: " + this.description + ", Priority: " + this.priority +
                ", Reported by: " + this.reporter + ", Reported on: " + dateFormat.format(this.dateReported) +
                ", Resolution: " + resolutionString + ", Date Resolved: " + dateResolved);
    }

}