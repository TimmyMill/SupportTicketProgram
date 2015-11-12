package TicketsOOP;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TicketManager {

    protected static Scanner scan;
    protected static LinkedList<Ticket> resolvedTickets = new LinkedList<>();

    public static void main(String[] args) {

        LinkedList<Ticket> ticketQueue = new LinkedList<>();
        scan = new Scanner(System.in);
        boolean quit = false;
        readFile(ticketQueue);

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
                    resolveTicket(ticketQueue);
                    break;
                }
                case 3: {
                    //delete a ticket by issue
                    System.out.println("Enter keyword to search for");
                    searchTicketList(ticketQueue, task);

                    //option to delete a ticket
                    System.out.println("Delete ticket?\nY or N");
                    String delete = scan.nextLine();
                    if (delete.equalsIgnoreCase("Y")) {
                        resolveTicket(ticketQueue);
                    }

                    break;
                }
                case 4: {
                    //search for a ticket by reporter's name
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
                    writeToFile(ticketQueue);
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

//    protected static void deleteTicket(LinkedList<Ticket> ticketQueue) {
    protected static void resolveTicket(LinkedList<Ticket> ticketQueue) {
        scan = new Scanner(System.in);

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        System.out.println("Enter ID of ticket to delete");
        int deleteID = validateIntInput();

        /* Loop over all tickets. Delete the one with this ticket ID */
        boolean found = false;
        for (Ticket ticket : ticketQueue) {
            if (ticket.getTicketID() == deleteID) {
                found = true;

                /* Resolve ticket */
                System.out.println("Enter how the problem was fixed or resolved");
                String resolution = scan.nextLine();

                ticket.setResolution(resolution);
                ticket.setDateResolved(new Date());
                resolvedTickets.add(ticket);

                ticketQueue.remove(ticket); //remove from queue
                System.out.println(String.format("Ticket %d deleted", deleteID));
                printAllTickets(ticketQueue);  //print updated list
                break; //don't need loop any more.
            }
        }
        if (!found) {
            System.out.println("Ticket ID not found, no ticket deleted");
            resolveTicket(ticketQueue);
        }
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
        boolean found = false;
        scan = new Scanner(System.in);
        String str = scan.nextLine();

        /* Loop over all tickets. Add the ones that have a particular keyword in them to a new list */
        for (Ticket ticket : ticketQueue) {
            switch (task) {

                /* Searches description */
                case 3: {
                    if (ticket.getDescription().toLowerCase().contains(str.toLowerCase())) {
                    //get description from ticket, convert to lowercase and check if it contains the string

                        matchesSearchString.add(ticket);
                        found = true;
                        continue;
                    }
                    break;
                }

                /* Searches names */
                case 4: {
                    if (ticket.getReporter().toLowerCase().contains(str.toLowerCase())) {
                    //get reporter from ticket, convert to lowercase and check if it contains the string

                        matchesSearchString.add(ticket);
                        found = true;
                        continue;
                    }
                    break;
                }
            }
        }

        /* If no matches are found, print that nothing was found.
         * Otherwise, print the list of tickets that match. */
        if (!found) {
            System.out.println("No matches found");
        }
        else {
            printAllTickets(matchesSearchString);
        }

        return matchesSearchString;
    }

    protected static void readFile(LinkedList<Ticket> ticketQueue) {
        File file = new File("open_tickets.txt");
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

        if (file.exists()) { //checks to make sure file exists before trying to read it

            /* Read from open_tickets.txt file */
            try (BufferedReader br = new BufferedReader(new FileReader("open_tickets.txt")))
            {
                String line = br.readLine();
                while (line != null) {

                    //description
                    String description = line.substring(line.indexOf("Issue:") + 7, line.indexOf("Priority:") - 2);

                    //priority
                    int priority = Integer.parseInt(line.substring(line.indexOf("Priority:") + 10, line.indexOf("Reported by:") - 2));

                    //reporter
                    String reporter = line.substring(line.indexOf("Reported by:") + 13, line.indexOf("Reported on:") - 2);

                    //dateReported
                    Date dateReported = dateFormat.parse(line.substring(line.indexOf("Reported on:") + 13, line.indexOf("Resolution:") - 2));

                    //Create tickets from open tickets text file
                    Ticket t = new Ticket(description, priority, reporter, dateReported);
                    addTicketInPriorityOrder(ticketQueue, t);

                    line = br.readLine();
                }
                br.close();
            }
            catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void writeToFile(LinkedList<Ticket> ticketQueue) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM_d_yyyy", Locale.ENGLISH); //used to format the date for resolved tickets filename
        Date date = new Date();

        /* Write open tickets to file */
        try (BufferedWriter unresolved = new BufferedWriter(new FileWriter("open_tickets.txt")))
        {
            for (Ticket u : ticketQueue) {
                unresolved.write(u.toString() + "\n");
            }
            unresolved.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        /* Write resolved tickets to file */
        if (resolvedTickets.size() > 0) { //If there are no tickets in the resolvedTickets list, a file will not be created

            try (BufferedWriter resolved = new BufferedWriter(new FileWriter("resolved_tickets_as_of_" + dateFormat.format(date) + ".txt", true)))
            { //Creates resolved tickets file. If the file already exists, true is used to append to the end of it.

                for (Ticket r : resolvedTickets) {
                    resolved.write(r.toString() + "\n");
                }
                resolved.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
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