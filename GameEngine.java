import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 *  This class is part of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.
 * 
 *  This class creates all rooms, creates the parser and starts
 *  the game.  It also evaluates and executes the commands that 
 *  the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (Jan 2003)
 */
public class GameEngine 
{
    private Parser parser;
    private Room currentRoom;
    private Stack<Room> previousRoom;
    private UserInterface gui;
        
    /**
     * Create the game and initialise its internal map.
     */
    public GameEngine() 
    {
        createRooms();
        previousRoom = new Stack<Room>();
        parser = new Parser();
    }

    public void setGUI(UserInterface userInterface)
    {
        gui = userInterface;
        printWelcome();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
        Room outside, theatre, pub, lab, office;
      
        // create the rooms
        outside = new Room("outside the main entrance of the university", "outside.gif");
        theatre = new Room("in a lecture theatre", "castle.gif");
        pub = new Room("in the campus pub", "courtyard.gif");
        lab = new Room("in a computing lab", "stairs.gif");
        office = new Room("the computing admin office", "dungeon.gif");
        
        // initialise room exits
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theatre.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);
        
        outside.addItem("caca", "A big shit", 10);
        outside.addItem("pipi", "It's quite sliding there...", 1);

        currentRoom = outside;  // start game outside
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
    	gui.print("\n");
    	gui.println("Welcome to Adventure!");
    	gui.println("Adventure is a new, incredibly boring adventure game.");
    	gui.println("Type 'help' if you need help.");
    	gui.print("\n");
        gui.println(currentRoom.getLongDescription());
        gui.showImage(currentRoom.getImageName());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    public void interpretCommand(String commandLine) 
    {
        gui.println(commandLine);
        Command command = parser.getCommand(commandLine);

        if(command.isUnknown()) {
            gui.println("I don't know what you mean...");
            return;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help"))
            printHelp();
        else if (commandWord.equals("go"))
            goRoom(command);
        else if (commandWord.equals("quit")) {
            if(command.hasSecondWord())
                gui.println("Quit what?");
            else
                endGame();
        }
        else if (commandWord.equals("look"))
        	look();
        else if (commandWord.equals("eat"))
        	eat();
        else if (commandWord.equals("back"))
        	back(command);
        else if (commandWord.equals("test"))
        	test(command);
        
        gui.print("\n");
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        gui.println("You are lost. You are alone. You wander");
        gui.println("around at Monash Uni, Peninsula Campus." + "\n");
        gui.print("Your command words are: " + parser.showCommands());
    }

    /** 
     * Try to go to one direction. If there is an exit, enter
     * the new room, otherwise print an error message.
     */
    private void goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            gui.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null)
            gui.println("There is no door!");
        else {
        	previousRoom.add(currentRoom);
            currentRoom = nextRoom;
            gui.println(currentRoom.getLongDescription());
            if(currentRoom.getImageName() != null)
                gui.showImage(currentRoom.getImageName());
        }
    }

    private void endGame()
    {
        gui.println("Thank you for playing.  Good bye.");
        gui.enable(false);
    }
    
    private void look()
    {
    	gui.println(currentRoom.getLongDescription());
    }
    
    private void eat()
    {
    	gui.println("You have eaten now and you are not hungry any more.");
    }

    private void back(Command command) 
    {
        if(command.hasSecondWord()) {
            gui.println("Back what?");
            return;
        }

        if (previousRoom.empty())
            gui.println("You can't go back!");
        else {
            currentRoom = previousRoom.pop();
            gui.print("\n");
            gui.println(currentRoom.getLongDescription());
            if(currentRoom.getImageName() != null)
                gui.showImage(currentRoom.getImageName());
        }
    }

    private void test(Command command) 
    {
        if (!command.hasSecondWord()) {
            gui.println("Test what?");
            return;
        }

        Scanner vScanner;
        
        try 
        { 
        	vScanner = new Scanner(new File("./" + command.getSecondWord()));
            while (vScanner.hasNextLine())
            {
                String ligne = vScanner.nextLine();
                interpretCommand(ligne);
            }
            vScanner.close();
        } 
        catch (FileNotFoundException pObjetException) 
        {  
        	gui.println("Le nom du fichier est incorrect.");
        } 
    }
}
