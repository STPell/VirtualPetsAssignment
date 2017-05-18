package virtualpets;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


/**
 * Main GUI class for VirtualPets sort of a ViewController.
 * @author Samuel Pell
 * @author Ollie Chick
 */
public class GUIMain implements Observer {
     /**
     * Frame in which all GUI elements appear.
     */
    private JFrame mainFrame;
    /**
     * Main model class.
     */
    private GameEnvironment mainGame;
    /**
     * Number of players to create.
     */
    private int playerNumber;
    /**
     * List of names to check for duplicates against.
     */
    private ArrayList<String> nameList = new ArrayList<String>();
    /**
     * Number of players that have been created by the ViewController.
     */
    private int numPlayersCreated = 0;
    /**
     * Home panel to display all in game info.
     */
    private HomePanel homeScreen;
    /**
     * The current player.
     */
    private Player currentPlayer;
    /**
     * The current pet.
     */
    private Pet currentPet;

    /**
     * Part of the Observer pattern to get data from GUI to GameEnvironment.
     * @param identifier Identifier of the operation.
     * @param values Array of strings appropriate to the GUI call.
     */
    public void getValues(String identifier, String[] values) {
        switch (identifier) {
            case "setup":
                Integer numDays = Integer.parseInt(values[1]);
                mainGame.setNumDays(numDays);
                playerNumber = Integer.parseInt(values[0]);
                clearFrame();
                createPlayer();
                System.out.println("Creating player 1");
                break;

            case "player creation":
                numPlayersCreated++;
                try {
                    mainGame.createPlayer(values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                clearFrame();
                if (numPlayersCreated < playerNumber) {
                    createPlayer();
                } else {
                    displayHome();
                }
                break;

            case "buy item":
                attemptBuyItem(values[0]);
                refreshScreen();
                break;

            case "sleep":
                currentPet.sleep();
                System.out.println("Sleeping");
                refreshScreen();
                break;

            case "toilet":
                currentPet.goToilet();
                System.out.println("Going toilet");
                refreshScreen();
                break;

            case "feed":
                Food food = mainGame.getFoodPrototypes().get(values[0]);
                currentPet.feed(food);
                refreshScreen();
                break;

            case "play":
                Toy toy = mainGame.getToyPrototypes().get(values[0]);
                play(toy);
                refreshScreen();
                break;

            default:
                System.out.println("Unknown GUI Element Identifier");
                System.out.println("Dropping data");
                System.out.println(identifier);
        }
    }

    private void play(Toy toy) {
        try {
            currentPet.play(toy);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("durability is zero or negative")) {
                //they've used the toy to the point of destruction
                JOptionPane.showMessageDialog(null, currentPet.getName()
                        + " broke the "
                        + toy.getName());
                currentPlayer.getToyList().remove(toy);
            } else {
                //unknown error
                throw e;
            }
        }


    }

    /**
     * Player attempts to buy an item.
     * @param itemBought Item player is trying to purchase
     */
    private void attemptBuyItem(String itemBought) {
        HashMap<String, Food> foodItems = mainGame.getFoodPrototypes();
        HashMap<String, Toy> toyItems = mainGame.getToyPrototypes();

        try { //first try to see if item is a food.
            Food purchasedItem = foodItems.get(itemBought);
            if (purchasedItem.getPrice() <= currentPlayer.getBalance()) {
                currentPlayer.spend(purchasedItem.getPrice());
                currentPlayer.addFood(purchasedItem);
            } else {
                String message = "You do not have enough money to purchase ";
                message += purchasedItem.getName();
                JOptionPane.showMessageDialog(null, message);
            }
        } catch (Exception e) {
            //Ignore first type exception.
        }

        try { //then see if item is a toy
            Toy purchasedItem = toyItems.get(itemBought);
            if (purchasedItem.getPrice() <= currentPlayer.getBalance()) {
                currentPlayer.spend(purchasedItem.getPrice());
                currentPlayer.addToy(purchasedItem);
            } else {
                String message = "You do not have enough money to purchase ";
                message += purchasedItem.getName();
                JOptionPane.showMessageDialog(null, message);
            }
        } catch (Exception e) {
            //ignore second exception
        }
    }

    /**
     * Refresh screen to display change in data.
     */
    private void refreshScreen() {
        homeScreen.refreshTabs(currentPlayer, currentPet, 1, 2);
    }

    /**
     * Displays home screen to the user and moves onto the first day.
     */
    private void displayHome(){
        clearFrame();
        mainFrame.setBounds(0, 0, 815, 540);

        int numDays = mainGame.getNumDays();
        homeScreen = new HomePanel(numDays, mainGame);

        //TODO register observer in other tabs
        homeScreen.getStoreTab().registerObserver(this);
        homeScreen.getFeedingTab().registerObserver(this);
        homeScreen.getSleepTab().registerObserver(this);
        homeScreen.getToiletTab().registerObserver(this);
        homeScreen.getPlayTab().registerObserver(this);

        mainFrame.getContentPane().add(homeScreen);
        homeScreen.setVisible(true);

        homeScreen.setSize(800, 500);

        newDay();
    }
    /**
     * Move to a new day. Currently doesn't do this...
     */
    private void newDay() {
        System.out.println("Its a brand new day!!");
        currentPlayer = mainGame.getPlayerList().get(0);
        mainGame.setCurrentPlayer(currentPlayer);
        currentPet = currentPlayer.getPetList().get(0);
        homeScreen.refreshTabs(currentPlayer, currentPet, 1, 2);
    }

    /**
     * Checks if a name is a duplicate.
     * @param name Name to be checked.
     * @param nameList List of names to check against
     * @return If the name is a duplicate.
     */
    public boolean nameTaken(String name, ArrayList<String> nameList) {
        boolean found = false;
        int i = 0;

        while (!found && (i < nameList.size())) {
            if (nameList.get(i).equals(name)) {
                found = true;
            }
            i++;
        }

        return found;
    }

    /**
     * Get a list of all registered names.
     * @return List of names.
     */
    public ArrayList<String> getRegisteredNames(){
        return nameList;
    }

    /**
     * Add a name to the list of names.
     * @param name Name to add to the list
     */
    public void registerName(String name) {
        nameList.add(name);
    }

    /**
     * Sets up the main window and game environment.
     */
    private void initialise() {
        mainGame = new GameEnvironment();
        mainGame.generateFoodPrototypes();
        mainGame.generateToyPrototypes();
        mainGame.initialiseNumGenerator(new String[0]);
        mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().setLayout(null);
    }

    /**
     * Shows the setup panel to the user.
     */
    private void showSetup() {
        mainFrame.setBounds(0, 0, 300, 200);
        SetupPanel setupPanel = new SetupPanel();
        setupPanel.registerObserver(this);

        mainFrame.getContentPane().add(setupPanel);
        setupPanel.setVisible(true);

        setupPanel.setSize(300, 165);

        mainFrame.setVisible(true);
    }

    /**
     * Display a player creation pane.
     */
    private void createPlayer() {
        mainFrame.setBounds(0, 0, 435, 392);
        PlayerCreationPanel playerCreation = new PlayerCreationPanel(this);
        playerCreation.registerObserver(this);

        mainFrame.getContentPane().add(playerCreation);
        playerCreation.setVisible(true);

        playerCreation.setSize(420, 355);

        mainFrame.setVisible(true);
    }

    /**
     * Clears the frames of all components.
     */
    private void clearFrame() {
        mainFrame.getContentPane().setVisible(false);
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().setVisible(true);
    }

    /**
     * Main method to kick everything off.
     * @param args Some arguments that probably won't get used.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            //ignore all exceptions
        	//TODO all these ignoring all exceptions seems like a bad idea?
        }
        GUIMain main = new GUIMain();
        main.initialise();
        main.showSetup();
    }

}
