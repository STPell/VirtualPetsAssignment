import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Command line interface for Virtual Pets game.
 * @author Samuel Pell
 * @author Ollie Chick
 *
 */
public class CommandLineInterface {

	private static String divider = "----------/----------";
	private static Scanner inputReader = new Scanner(System.in);

	/**
	 * Gets the number of days the game is to run for.
	 * @return Number of days to run game for.
	 */
	public static int getNumberOfDays(){
		Integer numDays = null;

		do{
			System.out.print("How many days do you want to play for? ");
			System.out.flush();
			String userInput = inputReader.next();
			try{
				numDays = Integer.parseInt(userInput);
				if (numDays < 1){ 
					//if number is 0 or less throw exception
					numDays = null;
					throw new NumberFormatException();
				}
			}catch (NumberFormatException exception){
				System.out.println("Please enter an integer greater than 0.");
			}
		}while (numDays == null);

		return numDays;
	}

	/**
	 * Used to ask the user the number of pets/players desired (between 1 and 3 inclusive).
	 * @param query Query to pose to user.
	 * @return Number of required pets or players.
	 */
	public static int getNumberRequired(String query){
		Integer numReq = null;
		do{
			System.out.print(query);
			System.out.flush();
			String userInput = inputReader.next();
			try{
				numReq = Integer.parseInt(userInput);
				if (numReq< 1 || numReq > 3){ 
					//if number is out of bounds 1-3 throw exception
					numReq = null;
					throw new NumberFormatException();
				}
			}catch (NumberFormatException exception){
				System.out.println("Please enter an integer between 1 and 3 inclusive.");
			}
		}while (numReq == null);

		return numReq;
	}

	/**
	 * Checks if a name is a duplicate.
	 * @param name Name to check for duplication.
	 * @param nameList List of already taken names.
	 * @return Whether or not the name is a duplicate.
	 */
	private static boolean nameTaken(String name, ArrayList<String> nameList){
		boolean found = false;
		int i = 0;

		while (!found && (i < nameList.size())){
			if (nameList.get(i).equals(name))
				found = true;
			i++;
		}

		return found;
	}

	/**
	 * Get the name of a player or pet.
	 * @param query Query to pose to user.
	 * @param nameList ArrayList of taken names.
	 * @return name of player or pet.
	 */
	public static String getName(String query, ArrayList<String> nameList){
		String name = null;

		do{
			System.out.print(query);
			System.out.flush();
			name = inputReader.next();
			if (nameTaken(name, nameList)){ //if name is already taken
				name = null;
				System.out.println("Duplicate names are not allowed.");
			}
		} while (name == null);

		nameList.add(name);
		return name;
	}


	//TODO: Ask Ollie how to handle IOException from Pet class
	/**
	 * Creates a pet object based on user input.
	 * @return pet of species desired by player.
	 * @throws IOException because it isn't handled at lower levels.
	 */
	public static Pet createPetSpecies() throws IOException{
		Pet newPet = new Cat(); //default to pet TODO is this meant to be Pet newPet = new Pet()? --Ollie
		String choice;
		//Get pet species
		System.out.println("1. Alpaca\n2. Cat\n3. Dog\n4. Goat\n5. Horse\n6. Polar bear");
		do{
			System.out.print("Which pet would you like? ");
			System.out.flush();

			choice = inputReader.next();

			switch(choice.toLowerCase()){
			case "1":
			case "alpaca":
			case "aplaca":
			case "apalca":
				newPet = new Alpaca();
				break;

			case "2":
			case "cat":
				newPet = new Cat();
				break;

			case "3":
			case "dog":
				newPet = new Dog();
				break;

			case "4":
			case "goat":
				newPet = new Goat();
				break;


			case "5":
			case "horse":
			case "hoarse":
				newPet = new Horse();
				break;

			case  "6":
			case "polar bear":
			case "poler bear":
			case "polarbear":
			case "polerbear":
			case "polar":
			case "poler":
			case "bear":
			case "beer":
			case "bare":
				newPet = new PolarBear();
				break;

			default:
				System.out.println("\n"+choice + " is not a valid option. Please enter one of the below choices.");
				System.out.println("1. Alpaca\n2. Cat\n3. Dog\n4. Goat\n5. Horse\n6. Polar bear");
				choice = null;
				break;
			}
		}while (choice == null);

		return newPet;
	}

	public static String[] listPrototypes(HashMap<String, Food> foodPrototypes, HashMap<String, Toy> toyPrototypes){
		String[] foodNames = foodPrototypes.keySet().toArray(new String[0]);
		String[] toyNames = toyPrototypes.keySet().toArray(new String[0]);
		String[] ordering = new String[foodNames.length + toyNames.length];
		Toy toy;
		Food food;

		System.out.println("----------- Food -----------");
		int currentOption = 1;
		for (int i = 0; i < foodNames.length; i++){
			food = foodPrototypes.get(foodNames[i]);
			System.out.println(currentOption + ". " + food + " Cost: $" + food.getPrice());
			ordering[i] = foodNames[i];
			currentOption++;
		}

		System.out.println("----------- Toys -----------");
		for (int i = 0; i< toyNames.length; i++){
			toy = toyPrototypes.get(toyNames[i]);
			System.out.println(currentOption + ". " + toy + " Cost: $" + toy.getPrice());
			ordering[i + foodNames.length] = toyNames[i];
			currentOption++;
		}

		return ordering;
	}

	/**
	 * Initialises a day.
	 * @param dayNumber the number of the current day.
	 */
	public static void newDay(int dayNumber){
		System.out.println("=== Day "+dayNumber+" ===");
	}

	/**
	 * Initialise a player's turn.
	 * @param player the player whose turn it is.
	 */
	public static void newPlayer(Player player){
		Boolean allDead = true;
		System.out.println("--- "+player.getName()+"'s turn ---");
		for (Pet pet : player.getPetList()){
			if(!pet.getIsDead()){allDead = false;}
		}
		if (allDead){
			System.out.println("Shame on you, "+player.getName()+", you killed all your pets.");
		}
	}

	/**
	 * Main game loop for one player to interact with one pet.
	 * @param toyPrototypes HashMap of all toys.
	 * @param foodPrototypes HashMap of all food.
	 * @param player the player whose turn it is; this player is currently interacting with their pet.
	 * @param pet the pet the player is interacting with.
	 * @throws Exception if error in code
	 */
	public static void interact(Player player, Pet pet, HashMap<String, Food> foodPrototypes, HashMap<String, Toy> toyPrototypes) throws Exception{
		int numOfActions = 2;
		String choice;
		if (pet.getIsDead()){
			numOfActions = 0;
		}
		while (numOfActions > 0){
			System.out.print("Hi "+player.getName()+"! You have "+numOfActions+" turns remaining today with "+pet.getName()
			+". What would you like to do?\n1. View pet status\n2. Visit the store\n3. Feed your pet\n4. Play with your pet\n"
			+"5. Put your pet to bed to sleep\n6. Let the pet go toilet\n7. Move on\n>>> ");
			choice = inputReader.next();
			switch(choice){
			case("1"):
				viewPetStatus(pet);
			break;
			case("2"):
				visitStore(player, foodPrototypes, toyPrototypes);
			break;
			case("3"):
				try{
					feedPet(player, pet);
					numOfActions--;
				}catch(Exception e){
					if(e.getMessage().equals("no food to eat")){
						System.out.println("Sorry, you don't have any food to feed your pet.");
					}else{
						throw e;
					}
				}
			break;
			case("4"):
				try{
					playWithPet(player, pet);
					numOfActions--;
				}catch(Exception e){
					if(e.getMessage().equals("no toys to play with")){
						System.out.println("Sorry, you don't have any toys to play with.");
					}else{
						throw e;
					}
				}
			break;
			case("5"):
				sleep(pet);
			numOfActions--;
			break;
			case("6"):
				goToilet(pet);
			numOfActions--;
			break;
			case("7"):
				//done with this pet today
				numOfActions = 0;
			break;
			default:
				System.out.println("I'm sorry. That's not a valid option. Please try again.");
			}
		}
	}

	private static void goToilet(Pet pet){
		pet.goToilet();		
		System.out.println("Your pet went to the toilet.");
	}

	private static void sleep(Pet pet){
		pet.sleep();		
		System.out.println("Your pet slept.");
	}

	private static void playWithPet(Player player, Pet pet) throws Exception{
		String choiceStr;
		int choice;
		Toy toy;
		if (player.getToyList().size() == 0){
			throw new Exception("no toys to play with");
		}
		System.out.print("Hi! ");
		do{
			System.out.println("What toy would you like your pet to play with?");
			int i=1;
			for(Toy playersToy : player.getToyList()){
				System.out.println(i+". "+playersToy);
				i++;
			}
			System.out.print(">>> ");
			System.out.flush();
			
			choiceStr = inputReader.next();
			try{
				choice = Integer.parseInt(choiceStr);
			}catch(Exception e){
				//not parsable as an Integer
				choice = 0;
			}
			
			if (choice<=0 || choice > i-1){
				choiceStr = null;
				System.out.println("Sorry, that's not a valid option.");
			}else{
				toy = player.getToyList().get(i-2);
				try{
					pet.play(toy);
				}catch(IllegalArgumentException e){
					if (e.getMessage().equals("durability is zero or negative")){
						//they've used the toy to the point of destruction
						player.getToyList().remove(i-2);
					}else{
						throw e;
					}
				}
			}
		} while (choiceStr == null);
	}

	private static void feedPet(Player player, Pet pet) throws Exception{
		String choiceStr;
		int choice;
		Food food;
		if (player.getFoodStock().size() == 0){
			throw new Exception("no food to eat");
		}
		System.out.print("Hi! ");
		do{
			System.out.println("What food would you like to feed your pet?");
			int i=1;
			for(Food playersFood : player.getFoodStock()){
				System.out.println(i+". "+playersFood);
				i++;
			}
			System.out.print(">>> ");
			System.out.flush();
			
			choiceStr = inputReader.next();
			try{
				choice = Integer.parseInt(choiceStr);
			}catch(Exception e){
				//not parsable as an Integer
				choice = 0;
			}
			
			if (choice<=0 || choice > i-1){
				choiceStr = null;
				System.out.println("Sorry, that's not a valid option.");
			}else{
				food = player.getFoodStock().get(i-2);
				pet.feed(food);
				player.getFoodStock().remove(i-2);
				}
		} while (choiceStr == null);
	}

	/**
	 * Store loop for player to purchase items from.
	 * @param player Player entering the store.
	 * @param foodPrototypes Hash map of the food item prototypes.
	 * @param toyPrototypes Hash map of the toy item prototypes.
	 * @throws Exception if there is an error in the game so what they're buying isn't a food or a toy
	 */
	private static void visitStore(Player player, HashMap<String, Food> foodPrototypes, HashMap<String, Toy> toyPrototypes) throws Exception{
		Boolean userWantsToStay = true;
		String choice;
		
		System.out.print("Hello " + player.getName() + ", welcome to the store. ");
		while(userWantsToStay){
			System.out.println("What do you want to do?\n1. View objects for sale\n2. View your items"
					+"\n3. Exit the store");
			System.out.print(">>> ");
			choice = inputReader.next();
			switch(choice){
			case("1"):
				userWantsToStay = buyFromStore(player, foodPrototypes, toyPrototypes);
				break;
			case("2"):
				printItems(player);
				break;
			case("3"):
				userWantsToStay = false;
				break;
			default:
				System.out.println("Sorry, that's not a valid option. Please try again.");
			}
			
		}
	}

	private static void printItems(Player player) {
		if (player.getToyList().size() == 0 && player.getFoodStock().size()==0){
			System.out.println("You have no items.");
		}else{
			for (Toy toy : player.getToyList()){
				System.out.println(toy);
			}
			for (Food food : player.getFoodStock()){
				System.out.println(food);
			}
		}
	}

	/**
	 * Lets the user buy something from the store.
	 * @param player Player buying something.
	 * @param foodPrototypes HashMap of foods.
	 * @param toyPrototypes HashMap of Toys.
	 * @return A boolean based on whether the user wants to stay in the store.
	 * @throws Exception if there is an error
	 */
	private static Boolean buyFromStore(Player player, HashMap<String, Food> foodPrototypes, HashMap<String, Toy> toyPrototypes) throws Exception{
		String choice;
		String type = null;
		String purchasedItemName = null;
		Toy purchasedToy = null;
		Food purchasedFood = null;
		int maxPossibleChoice;
		int i;

		do{
			System.out.println("Hello " + player.getName() + ", you have $"+player.getBalance()+". What would you like to buy today?");
			String[] ordering = listPrototypes(foodPrototypes, toyPrototypes);
			System.out.println(ordering.length+1 + ". Exit the store");

			System.out.print(">>> ");
			System.out.flush();

			choice = inputReader.next();
			maxPossibleChoice = foodPrototypes.size() + toyPrototypes.size() + 1;
			if (Integer.parseInt(choice) < 1 || Integer.parseInt(choice) > maxPossibleChoice){
				choice = null;
				System.out.println("Sorry, that's not a valid option.");
			}else{
				i = Integer.parseInt(choice);
				if(i>=1 && i <= foodPrototypes.size()){
					type = "food";
					purchasedItemName = ordering[i-1];
					purchasedFood = foodPrototypes.get(purchasedItemName);
				}else if(i > foodPrototypes.size() && i < maxPossibleChoice){
					type = "toy";
					purchasedItemName = ordering[i-1];
					purchasedToy = toyPrototypes.get(purchasedItemName);
				}else if(i==maxPossibleChoice){
					type = "leave";
				}else{
					//error
					throw new Exception("Error: not a toy or a food");
				}
			}

		} while (choice == null);

		if(type=="toy"){
			try{
				player.spend(purchasedToy.getPrice());
				player.addToy(purchasedToy);
				System.out.println("You have bought: "+ purchasedItemName);
			}catch(IllegalArgumentException e){
				System.out.println("Sorry, you don't have enough money for that. You have $"+player.getBalance()+" and that item costs $"
						+purchasedToy.getPrice()+".");
			}
			return true;
		}else if(type=="food"){
			try{
				player.spend(purchasedFood.getPrice());
				player.addFood(purchasedFood);
				System.out.println("You have bought: "+ purchasedItemName);
			}catch(IllegalArgumentException e){
				System.out.println("Sorry, you don't have enough money for that. You have $"+player.getBalance()+" and that item costs $"
						+purchasedFood.getPrice()+".");
			}
			return true;
		}else if(type == "leave"){
			return false;
		}else{
			return true;
		}
	}

	private static void viewPetStatus(Pet pet){
		System.out.println(divider);
		System.out.println("Status of "+pet.getName()+":\nGender: "+pet.getGender()
		+"\nSpecies: "+pet.getSpecies()
		+"\nFatigue: "+pet.getFatigue()
		+"\nHappiness: "+pet.getHappiness()
		+"\nHealth: "+pet.getHealth()
		+"\nHunger: "+pet.getHunger()
		+"\nMischievousness: "+pet.getMischievousness()
		+"\nPercent bladder full: "+pet.getPercentBladderFull()
		+"\nWeight: "+pet.getWeight()
		+"\nIs misbehaving: "+pet.getIsMisbehaving()
		+"\nIs revivable: "+pet.getIsRevivable()
		+"\nIs sick: "+pet.getIsSick());
		System.out.println(divider);
	}

	/**
	 * If the pet is about to misbehave, this gets run to see if the user wants to discipline
	 * @return true if they choose to discipline
	 */
	public static Boolean petMisbehaves() {
		String choiceStr;
		Boolean choice = null;
		
		System.out.print("WARNING! YOUR PET IS MISBEHAVING! ");
		do{
			System.out.println("DO YOU WANT TO DISCIPLINE? (Y/N)");
			choiceStr = inputReader.next();
			if (choiceStr.toLowerCase().equals("y")){
				choice = true;
			}else if(choiceStr.toLowerCase().equals("n")){
				choice = false;
			}else{
				choiceStr = null;
				System.out.print("Sorry, that's not a valid option. ");
			}
		}while(choiceStr == null);
		return choice;
	}

	/**
	 * This gets run if the pet gets sick to see if the user wants to pay for treatment (if they can afford it).
	 * @param balance the user's current balance
	 * @return whether or not the user healed them
	 */
	public static Boolean petSicks(int balance) {
		String choiceStr;
		Boolean choice = null;
		
		System.out.print("WARNING! YOUR PET HAS BECOME SICK! ");
		if(balance >= 50){
			do{
				System.out.println("DO YOU WANT TO PAY $50 FOR TREATMENT? YOU CURRENTLY HAVE $"+balance+". (Y/N)");
				choiceStr = inputReader.next();
				if (choiceStr.toLowerCase().equals("y")){
					choice = true;
				}else if(choiceStr.toLowerCase().equals("n")){
					choice = false;
				}else{
					choiceStr = null;
					System.out.print("Sorry, that's not a valid option. ");
				}
			}while(choiceStr == null);
		}else{
			System.out.println("UNFORTUNATELY, TREATMENT COSTS $50 BUT YOU ONLY HAVE$"+balance+".");
			choice = false;
		}
		return choice;
	}

	/**
	 * This gets run if the pet dies.
	 * @param revivable if the pet is revivable.
	 * @return whether or not the user revived them.
	 */
	public static Boolean petDies(Boolean revivable) {
		String choiceStr;
		Boolean choice = null;
		
		System.out.print("WARNING! YOUR PET HAS UNEXPECTEDLY DIED! ");
		if(revivable){
		do{
			System.out.println("DO YOU WANT TO REVIVE THEM? (Y/N)");
			choiceStr = inputReader.next();
			if (choiceStr.toLowerCase().equals("y")){
				choice = true;
			}else if(choiceStr.toLowerCase().equals("n")){
				choice = false;
			}else{
				choiceStr = null;
				System.out.print("Sorry, that's not a valid option. ");
			}
		}while(choiceStr == null);
		}else{
			System.out.println("RIP.");
			choice = false;
		}
		return choice;
	}

	/**
	 * Tidy up to close gracefully.
	 */
	public static void tearDown(){
		inputReader.close();
	}

	public static void postGame(Player[] playerList){
		System.out.println("That's the end of the game. And the results are in:");
		for (Player player : playerList){
			System.out.println(player.getName()+" has a score of "+player.calculateAndGetScore());
		}
	}

}
