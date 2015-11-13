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
        readFile(ticketQueue); //read file in when program starts

        /* If there are already open tickets that were pulled in and created from the text file, the ticket queue will not be empty.
         * TicketIDCounter should start at the next number after the highest ID from the text file */

        if (!ticketQueue.isEmpty()) { //if ticket queue isn't empty
            int max = 0;
            for (Ticket t : ticketQueue) {
                if (t.getTicketID() > max) {
                    max = t.getTicketID();
                }
            }
            Ticket.setStaticTicketIDCounter(max + 1);
        }

        while(!quit){

            System.out.println("1. Enter Ticket");
            System.out.println("2. Delete by ID");
            System.out.println("3. Delete by Issue");
            System.out.println("4. Search by Name");
            System.out.println("5. Display All Tickets");
            System.out.println("6. Quit");

            int task = validateIntInput();

            switch (task) {
                /* Create a ticket */
                case 1: {
                    //Call addTickets, which will let us enter any number of new tickets
                    addTickets(ticketQueue);
                    break;
                }

                /* Delete a ticket by ID */
                case 2: {
                    printAllTickets(ticketQueue);
                    resolveTicket(ticketQueue);
                    break;
                }

                /* Delete a ticket by issue */
                case 3: {
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

                /* Search for a ticket by reporter's name */
                case 4: {
                    System.out.println("Enter name to search for");
                    searchTicketList(ticketQueue, task);
                    break;
                }

                /* Print all tickets */
                case 5: {
                    printAllTickets(ticketQueue);
                    break;
                }

                /* Quit. Saves all tickets to a file */
                case 6: {
                    writeToFile(ticketQueue);
                    quit = true;
                    System.out.println("Quitting program");
                    break;
                }

                /* Default. If user enters a number that doesn't correspond to a menu option,
                   they are prompted to enter a number from the menu */
                default: {
                    System.out.println("Please enter a number from the menu above");
                }
            }
        }
        scan.close();
    }

    /* Deletes a ticket by using ticket ID. If ticket is deleted, it is assumed that problem was resolved.
     * Will ask for a resolution and set the resolution date the the current date.
     * Then it adds the ticket to a new list of resolved tickets and removes from the ticket queue.  */
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
                found = true; //change boolean value found to true if ticket is found

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

        if (!found) { //if ticket isn't found, print that it wasn't and rerun method
            System.out.println("Ticket ID not found, no ticket deleted");
            resolveTicket(ticketQueue); //reruns method
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
        /* Will only get here if the ticket is not added in the loop.
         * If that happens, it must be lower priority than all other tickets. So, add to the end. */
        tickets.addLast(newTicket);
    }

    /* Prints all tickets */
    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");

        tickets.forEach(System.out::println);

        System.out.println(" ------- End of ticket list ----------");
    }

    /* Searches through the ticket queue and looks for any tickets that match a user entered keyword.
     * If any matches are found, a list containing those tickets is returned.
     */
    protected static LinkedList<Ticket> searchTicketList(LinkedList<Ticket> ticketQueue, int task) {
        LinkedList<Ticket> matchesSearchString = new LinkedList<>(); //list to hold tickets that match keyword
        boolean found = false;
        scan = new Scanner(System.in);
        String str = scan.nextLine();

        /* Loop over all tickets. Add the ones that have a particular keyword in them to a new list */
        for (Ticket ticket : ticketQueue) {
            switch (task) { //Switch looks uses int value of task to decide which case to enter

                /* Searches description */
                case 3: {
                    if (ticket.getDescription().toLowerCase().contains(str.toLowerCase())) {
                    //get description from ticket, convert to lowercase and check if it contains the string

                        matchesSearchString.add(ticket); //adds matching ticket to list
                        found = true; //boolean found is changed to true
                        continue; //we use a continue to keep checking the rest of the list
                    }
                    break;
                }

                /* Searches names */
                case 4: {
                    if (ticket.getReporter().toLowerCase().contains(str.toLowerCase())) {
                    //get reporter from ticket, convert to lowercase and check if it contains the string

                        matchesSearchString.add(ticket); //adds matching ticket to list
                        found = true; //boolean found is changed to true
                        continue; //we use a continue to keep checking the rest of the list
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

    /* Reads in the open tickets text file to make ticket objects that were "saved" from the last session */
    protected static void readFile(LinkedList<Ticket> ticketQueue) {
        File file = new File("open_tickets.txt");
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        /* Used the idea from the site below to format the Date because a lot of Date's methods are deprecated.
         * http://stackoverflow.com/questions/4216745/java-string-to-date-conversion */

        if (file.exists()) { //checks to make sure file exists before trying to read it

            /* Read from open_tickets.txt file */
            try (BufferedReader br = new BufferedReader(new FileReader("open_tickets.txt")))
            {
                String line = br.readLine();

                /* This loop will read each line, one at a time until the end of the file.
                 * Information stored in each line is pulled out by using substrings to "capture" that piece of information.
                 * The indexOf String method is used to mark where to start and end each substring.
                 * The pieces that make up a ticket object are pulled from each line and used to create new tickets */

                while (line != null) {

                    //description
                    String description = line.substring(line.indexOf("Issue:") + 7, line.indexOf("Priority:") - 2);

                    //priority
                    int priority = Integer.parseInt(line.substring(line.indexOf("Priority:") + 10, line.indexOf("Reported by:") - 2));

                    //reporter
                    String reporter = line.substring(line.indexOf("Reported by:") + 13, line.indexOf("Reported on:") - 2);

                    //dateReported
                    Date dateReported = dateFormat.parse(line.substring(line.indexOf("Reported on:") + 13, line.indexOf("Resolution:") - 2));

                    //ticketID
                    int ticketID = Integer.parseInt(line.substring(4, line.indexOf(",")));

                    //Create tickets from open tickets text file
                    Ticket t = new Ticket(description, priority, reporter, dateReported);
                    t.setTicketID(ticketID); //sets the ticket ID when object is created so that it doesn't start over from 1
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

    /* Writes the data from all of the open tickets and resolved tickets into two files */
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