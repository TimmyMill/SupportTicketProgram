package TicketsOOP;

import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class TicketManager {

    protected static Scanner scan;

    public static void main(String[] args) {

        LinkedList<Ticket> ticketQueue = new LinkedList<>();

        scan = new Scanner(System.in);

        boolean quit = false;

        while(!quit){

            System.out.println("1. Enter Ticket");
            System.out.println("2. Delete by ID");
            System.out.println("3. Delete by Issue");
            System.out.println("4. Search by Name");
            System.out.println("5. Display All Tickets");
            System.out.println("6. Quit");

            int task = validateIntInput();

            switch (task) {
                case 1: {
                    //Call addTickets, which will let us enter any number of new tickets
                    addTickets(ticketQueue);
                    break;
                }
                case 2: {
                    //delete a ticket by ID
                    System.out.println("Enter ID of ticket to delete");
                    deleteTicket(ticketQueue);
                    break;
                }
                case 3: {
                    //delete a ticket by issue
                    System.out.println("Enter keyword to search for");
                    searchTicketList(ticketQueue, task);
                    break;
                }
                case 4: {
                    //search a ticket by name
                    System.out.println("Enter name to search for");
                    searchTicketList(ticketQueue, task);
                    break;
                }
                case 5: {
                    //print all tickets
                    printAllTickets(ticketQueue);
                    break;
                }
                case 6: {
                    //Quit. Future prototype may want to save all tickets to a file
                    quit = true;
                    System.out.println("Quitting program");
                    break;
                }
                default: {
                    System.out.println("Please enter a number from the menu above");
                }
            }
        }
        scan.close();
    }

    protected static void deleteTicket(LinkedList<Ticket> ticketQueue) {

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        int deleteID = validateIntInput();

        /* Loop over all tickets. Delete the one with this ticket ID */
        boolean found = false;
        for (Ticket ticket : ticketQueue) {
            if (ticket.getTicketID() == deleteID) {
                found = true;
                ticketQueue.remove(ticket);
                System.out.println(String.format("Ticket %d deleted", deleteID));
                break; //don't need loop any more.
            }
        }
        if (!found) {
            System.out.println("Ticket ID not found, no ticket deleted");
            deleteTicket(ticketQueue);
        }
        printAllTickets(ticketQueue);  //print updated list
    }

    /* Move the adding ticket code to a method */
    protected static void addTickets(LinkedList<Ticket> ticketQueue) {
        Scanner sc = new Scanner(System.in);

        boolean moreProblems = true;
        String description;
        String reporter;

        /* Let's assume all tickets are created today, for testing. We can change this later if needed */
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        while (moreProblems){
            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            priority = validateIntInput();

            Ticket t = new Ticket(description, priority, reporter, dateReported);
            //ticketQueue.add(t);
            addTicketInPriorityOrder(ticketQueue, t);

            /* To test, let's print out all of the currently stored tickets */
            printAllTickets(ticketQueue);

            System.out.println("More tickets to add?\nY or N");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }

    }
    protected static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket){

        /* Logic: assume the list is either empty or sorted */

        if (tickets.size() == 0 ) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        /* Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire) */
        /* Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end */

        int newTicketPriority = newTicket.getPriority();
        for (int x = 0; x < tickets.size() ; x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        /*
        Will only get here if the ticket is not added in the loop
        If that happens, it must be lower priority than all other tickets. So, add to the end.
        */
        tickets.addLast(newTicket);
    }

    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");

        tickets.forEach(System.out::println);

        System.out.println(" ------- End of ticket list ----------");
    }
    
    protected static LinkedList<Ticket> searchTicketList(LinkedList<Ticket> ticketQueue, int task) {
        LinkedList<Ticket> matchesSearchString = new LinkedList<>();
        scan = new Scanner(System.in);
        String str = scan.nextLine();

        /* Loop over all tickets. Add the ones that have a particular keyword in them to a new list */
        for (Ticket ticket : ticketQueue) {
            switch (task) {
                case 3: {
                    // Searches description
                    if (ticket.getDescription().contains(str)) {
                        matchesSearchString.add(ticket);
                        continue;
                    }
                    break;
                }
                case 4: {
                    // Searches names
                    if (ticket.getReporter().contains(str)) {
                        matchesSearchString.add(ticket);
                        continue;
                    }
                    break;
                }
                default: {
                    System.out.println("No matches found");
                }
            }
        }
        printAllTickets(matchesSearchString);
        return matchesSearchString;
    }

    /* Validation Method */
    protected static int validateIntInput() {
        scan = new Scanner(System.in);
        String str = scan.nextLine(); //take user input as a string since it's easier to validate

        /* If user enters anything other than a positive number, ask for input again */
        while (!str.matches("[0-9]+")) {
            System.out.println("Please enter a positive number");
            str = scan.nextLine();
        }
        return Integer.parseInt(str); //parse user input from a string to an int
    }

}