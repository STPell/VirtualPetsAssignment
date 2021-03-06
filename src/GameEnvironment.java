import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Launching off point for Virtual Pets game.
 *
 * @author Samuel Pell
 * @author Ollie Chick
 *
 */
public class GameEnvironment {

    /**
     * The list of players.
     */
    private Player[] playerList;
    /**
     * The list of names used so far. This is used to avoid duplicate names.
     */
    private ArrayList<String> nameList = new ArrayList<String>();
    /**
     * HashMap of all foods. It maps the name of the food to an instance of the
     * food.
     */
    private HashMap<String, Food> foodPrototypes;
    /**
     * HashMap of all toys.
     * It maps the name of the toy to an instance of the toy.
     */
    private HashMap<String, Toy> toyPrototypes;
    /**
     * The current day number.
     */
    private int dayNumber;
    /**
     * The total number of days the game will run for.
     */
    private int numberOfDays;
    /**
     * How much each player gets per pet per day, in dollars.
     */
    private int dailyPetAllowance;
    /**
     * The random number generated, used for random events. Random events
     * include misbehaving, being sick, and dying.
     */
    private Random randomNumGen;

    /**
     * Sets the name for a new player.
     *
     * @param newPlayer
     *            Player object to set name for.
     */
    private void setPlayerName(Player newPlayer) {
        try {
            newPlayer.setName(CommandLineInterface.getName("Player name: ", nameList));
        } catch (IllegalArgumentException exception) {
            System.out.println("Unknown error. Please try again.");
        }
    }

    /**
     * Gets lines 2 onwards from a data file specified.
     *
     * @param fileName
     *            File to get data from.
     * @return ArrayList of each line as a string.
     */
    private ArrayList<String> getDataFromFile(String fileName) {
        String line;
        ArrayList<String> data = new ArrayList<String>();

       try {
            Reader inputFile;
            try { //Runs if running class directly
                String topDir = System.getProperty("user.dir");
                if (topDir.endsWith("bin")) { //from cmdln
                    fileName = "../../src/" + fileName;
                } else { //from eclipse
                    fileName = "src/" + fileName;
                }
                inputFile = new FileReader(fileName);
            } catch (FileNotFoundException e) { //if running from jar file.
                fileName = "/" + fileName;
                InputStream stream = this.getClass().getResourceAsStream(fileName);
                inputFile = new InputStreamReader(stream);
            }

            BufferedReader bufferReader = new BufferedReader(inputFile);
            bufferReader.readLine(); //ignore first line

            while ((line = bufferReader.readLine()) != null) {
                data.add(line);
            }

            bufferReader.close(); //tidy up after reading file
            inputFile.close();
        } catch (IOException e) {
            //If there is an IO error here just give up.
            System.err.println("Error while reading file line by line: " + e.getMessage());
            System.exit(0);
        }

        return data;
    }

    /**
     * Parses a line into food or toy info to create prototype items
     *
     * Takes a line read from a data file, splits it, and then iterates along
     * the columns. Maps the columns to data fields and then returns an object
     * array of the information
     *
     * @param line
     *            String taken from either foodData or toyData files
     * @param mapping
     *            Mapping generated from the first line of columns to fields
     * @return Object array in format {String name, String description, Integer
     *         price, Integer size, String[] speciesOrder, Integer[] increase}
     */
    private String[][] parseLine(String line, HashMap<Integer, String> mapping) {
        String[] splitLine = line.split(",");

        // Create returnable fields
        String[] name = new String[1];
        String[] description = new String[1];
        String[] price = new String[1];
        String[] size = new String[1];
        String[] increase = new String[mapping.keySet().size() - 4]; // number
        // of
        // columns
        // -
        // number
        // of
        // columns
        // common
        // to
        // each
        // animal
        String[] speciesOrder = new String[mapping.keySet().size() - 4];

        int i = 0;
        for (int col = 0; col < splitLine.length; col++) { // for each column
            // look at the field
            // header and decide
            // where the data
            // lives
            switch (mapping.get(col)) {
            case "name":
                name[0] = splitLine[col];
                break;
            case "description":
                description[0] = splitLine[col];
                break;
            case "price":
                price[0] = splitLine[col];
                break;
            case "durability":
            case "portionSize":
                size[0] = splitLine[col];
                break;
            default:
                if (mapping.get(col).substring(0, 17).equals("increaseHappiness")) {
                    speciesOrder[i] = formatSpeciesName(mapping.get(col).substring(17));
                    increase[i] = splitLine[col];
                    i++;
                } else if (mapping.get(col).substring(0, 14).equals("increaseHealth")) {
                    speciesOrder[i] = formatSpeciesName(mapping.get(col).substring(14));
                    increase[i] = splitLine[col];
                    i++;
                }
                break;
            }
        }

        // This isn't very Javalike but it reduces code duplication
        return new String[][] {name, description, price, size, speciesOrder, increase};
    }

    /**
     * Format species name in a uniform way.
     *
     * In the item data csv files the headings aren't formatted the same way as
     * the pet species are used in code. This function converts between them. It
     * does this by making the species header lower case, comparing this against
     * a list of special cases, applying any necessary changes and returning
     * that formatted string.
     *
     * @param unformatted
     *            unformatted species name
     * @return formatted species name
     */
    private String formatSpeciesName(String unformatted) {
        String formatted;
        unformatted = unformatted.toLowerCase();
        switch (unformatted) {
        case "polarbear":
            formatted = "polar bear";
            break;
        default:
            formatted = unformatted;
            break;
        }
        return formatted;
    }

    /**
     * Generates all food prototypes for game from the file foodData.csv.
     */
    public void generateFoodPrototypes() {
        foodPrototypes = new HashMap<String, Food>();
        ArrayList<String> data = getDataFromFile("foodData.csv");
        //Create map of column number to fields
        HashMap<Integer, String> mapping = new HashMap<Integer, String>();

        String[] firstLine = data.get(0).split(",");
        data.remove(0); // remove the first line so later iteration is easier

        for (int i = 0; i < firstLine.length; i++) {
            mapping.put(i, firstLine[i]); // create mapping of columns to fields
        }

        for (String line : data) {
            String[][] information = parseLine(line, mapping);
            String name = information[0][0];
            String description = information[1][0];
            int price = Integer.parseInt(information[2][0]);
            int portionSize = Integer.parseInt(information[3][0]);
            Food newFood = new Food(name, description, price, portionSize);
            newFood.setHealthIncrease(information[4], information[5]);

            foodPrototypes.put(name, newFood);
        }
    }

    /**
     * Generates all toy prototypes for game from the file toyData.csv.
     */
    public void generateToyPrototypes() {
        toyPrototypes = new HashMap<String, Toy>();
        ArrayList<String> data = getDataFromFile("toyData.csv");
        //Create map of column number to fields
        HashMap<Integer, String> mapping = new HashMap<Integer, String>();

        String[] firstLine = data.get(0).split(",");
        data.remove(0); // remove the first line so later iteration is easier

        for (int i = 0; i < firstLine.length; i++) {
            mapping.put(i, firstLine[i]); // create mapping of columns to fields
        }

        for (String line : data) {
            String[][] information = parseLine(line, mapping);

            String name = information[0][0];
            String description = information[1][0];
            int price = Integer.parseInt(information[2][0]);
            int durability = Integer.parseInt(information[3][0]);

            Toy newToy = new Toy(name, description, price, durability);
            newToy.setHappinessIncrease(information[4], information[5]);

            toyPrototypes.put(name, newToy);
        }
    }

    /**
     * Creates a pet for a player.
     *
     * @return Pet player has made.
     */
    private Pet createPet() {
        Pet newPet = CommandLineInterface.createPetSpecies();

        try {
            newPet.setName(CommandLineInterface.getName("Pet name: ", nameList));
        } catch (IllegalArgumentException exception) {
            System.out.println("Unknown error. Please try again.");
        }

        Boolean genderDecider = randomNumGen.nextBoolean();
        if (genderDecider) { // gender decided by randomNumGen
            newPet.setGender("female");
        } else {
            newPet.setGender("male");
        }

        return newPet;
    }

    /**
     * Creates a new player.
     *
     * @return A fully created player
     */
    private Player createPlayer() {
        Player newPlayer = new Player();
        setPlayerName(newPlayer);
        int numPets = CommandLineInterface.getNumberRequired("Hi "
                + newPlayer.getName()
                + "! How many pets do you want? ");

        ArrayList<Pet> playerPetList = newPlayer.getPetList();
        Pet newPet;
        for (int i = 0; i < numPets; i++) {
            newPet = createPet();
            playerPetList.add(newPet);
        }
        return newPlayer;
    }

    /**
     * Performs all setup for the game.
     *
     * Creates players, their pets, and a prototype of each Toy and Food.
     *
     * @throws IOException
     *             Pet creation side effect
     */
    private void setup() throws IOException {
        CommandLineInterface.gameHeader();
        CommandLineInterface.tutorial();
        numberOfDays = CommandLineInterface.getNumberOfDays();
        dayNumber = 1;
        int numPlayers = CommandLineInterface.getNumberRequired("How many players? ");
        playerList = new Player[numPlayers];

        for (int i = 0; i < numPlayers; i++) {
            playerList[i] = createPlayer();
        }

        generateToyPrototypes();
        generateFoodPrototypes();
    }

    /**
     * Tears down the game.
     */
    private void tearDown() {
        CommandLineInterface.tearDown();
    }

    /**
     * Sets up random number generator for testing.
     *
     * @param args
     *            Only argument is a seed of type long for the generator.
     */
    public void initialiseNumGenerator(String[] args) {
        if (args.length == 1) {
            randomNumGen = new Random(Long.parseLong(args[0]));
        } else {
            randomNumGen = new Random();
        }
    }

    /**
     * Method to return a list of food prototypes - for testing purposes.
     *
     * @return HashMap of food prototypes mapping from food name to food prototype.
     */
    public HashMap<String, Food> getFoodPrototypes() {
        return foodPrototypes;
    }

    /**
     * Method to return a list of toy prototypes - for testing purposes.
     * @return HashMap of toy prototypes mapping from toy name to toy prototype.
     */
    public HashMap<String, Toy> getToyPrototypes() {
        return toyPrototypes;
    }

    /**
     * After the game, tells the user the scores, etc.
     * @throws Exception if error in code
     */
    private void postGame() throws Exception {
        for (int i = 0; i < playerList.length; i++) {
            playerList[i].calculateScore();
        }
        Player[] rankedPlayers = rankPlayers();
        CommandLineInterface.postGame(rankedPlayers);
    }

    /**
     * Main game loop.
     * @throws Exception if error in code
     */
    private void gameLoop() throws Exception {
        dailyPetAllowance = 10;

        while (dayNumber <= numberOfDays) {
            CommandLineInterface.newDay(dayNumber);

            for (Player player : playerList) {
                CommandLineInterface.newPlayer(player);
                int numOfAlivePets = 0;
                for (Pet pet : player.getPetList()) { // count up all the alive
                    // pets
                    if (!pet.getIsDead()) {
                        numOfAlivePets++;
                    }
                }
                player.earn(dailyPetAllowance * numOfAlivePets);

                for (Pet pet : player.getPetList()) {
                    if (!pet.getIsDead()) { // if the pet isn't dead
                        newDayPetActions(player, pet);
                        CommandLineInterface.interact(player, pet, foodPrototypes, toyPrototypes);
                    }
                }
                player.calculateScore();
            }

            dayNumber++;
        }
    }

    /**
     * This runs just before a user interacts with their pet. It does three
     * things: 1. Increases the pet's fatigue, decreases its happiness,
     * increases its hunger, and increases its mischievousness. 2. If the pet is
     * very tired, it decreases its health. 3. Controls if the pet does random
     * events, such as misbehaving, getting sick, and dying.
     *
     * @param player
     *            the player who is interacting with their pet.
     * @param pet
     *            the pet the player is about to interact with.
     */
    private void newDayPetActions(Player player, Pet pet) {
        Boolean disciplined;
        Boolean treated;
        Boolean revived;

        pet.increaseFatigue(30);
        pet.increaseHappiness(-10);
        pet.increaseHunger(30);
        pet.increaseMischievousness(5);

        // If fatigue is especially high, reduce health.
        int fatigue = pet.getFatigue();
        if (fatigue > 80) {
            pet.increaseHealth(-10);
        } else if (fatigue > 90) {
            pet.increaseHealth(-25);
        } else if (fatigue == 100) {
            pet.increaseHealth(-50);
        }

        /*
         * Check if misbehaving
         */
        // create random number between 0 and 99
        int randomNumber = randomNumGen.nextInt(100);
        int wellness = (pet.getHappiness() * 3
                + pet.getHealth()
                + (100 - pet.getMischievousness()) * 5
                + (100 - pet.getHunger())) / 10;
        if (wellness < 25 && randomNumber < 75
                || wellness < 50 && randomNumber < 50
                || wellness < 75 && randomNumber < 25) {
            disciplined = CommandLineInterface.petMisbehaves(pet);
            if (disciplined) {
                pet.discipline();
            } else {
                pet.misbehave();
            }
        }

        /*
         * Check if sick
         */
        // create random number between 0 and 99
        randomNumber = randomNumGen.nextInt(100);
        int health = pet.getHealth();
        if (pet.getIsSick()
                || health < 5
                || health < 25 && randomNumber < 75
                || health < 50 && randomNumber < 50
                || health < 75 && randomNumber < 25) {
            treated = CommandLineInterface.petSicks(pet, player.getBalance());
            if (treated) {
                pet.treat();
                player.spend(50);
            } else {
                pet.beSick();
            }
        }

        /*
         * Check if dead
         */
        // create random number between 0 and 99
        randomNumber = randomNumGen.nextInt(100);
        if (pet.getIsSick() && pet.getHappiness() < 50 || health < 5 || randomNumber < 2) {
            revived = CommandLineInterface.petDies(pet, pet.getIsRevivable());
            if (revived) {
                pet.revive();
            } else {
                pet.die();
            }
        }

    }

    /**
     * Ranks players based on score in descending order of score. Assumes all
     * scores have been calculated beforehand.
     *
     * @return ranked list of players.
     */
    public Player[] rankPlayers() {
        // Add players to an ArrayList
        ArrayList<Player> rankedList = new ArrayList<Player>();
        for (int i = 0; i < playerList.length; i++) {
            rankedList.add(playerList[i]);
        }

        // Sort the players
        rankedList.sort(null);

        // Convert ArrayList to array and return array
        Player[] rankedArray = new Player[rankedList.size()];
        rankedArray = rankedList.toArray(rankedArray);

        return rankedArray;
    }

    /**
     * Method purely for testing purposes. Overwrites existing playerList with
     * its own array of players.
     *
     * @param playerArray
     *            Fully setup list of players.
     */
    protected void addPlayers(Player[] playerArray) {
        playerList = playerArray;
    }

    /**
     * Main entry point.
     *
     * @param args
     *            Arguments - don't really have many of them.
     * @throws IOException
     *             When a file has an issue.
     */
    public static void main(String[] args) throws IOException {
        GameEnvironment mainGame = new GameEnvironment();

        mainGame.initialiseNumGenerator(args);
        mainGame.setup();
        try {
            mainGame.gameLoop();
            mainGame.postGame();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainGame.tearDown();

    }
}
