import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Game {

	Set<Card> envelope = new HashSet<>();
	List<Player> players = new ArrayList<>();
	Board board;
	boolean isOver = false;

	public static void main(String[] args) {
		Game game = new Game();
		game.initialise();
	}

	public void initialise() {
		Scanner sc = new Scanner(System.in);
		System.out.println("*************************************************\n"
						 + "*Welcome to Cluedo! How many people are playing?*\n"
						 + "*************************************************");
		//get the number of players playing
		int players = -1;
		do {
			players = getNumberPlayers(sc);
		} while(players == -1);
		
		//get the list of all possible players
		List<PersonCard.PersonType> characters = new ArrayList<>(Arrays.asList(PersonCard.PersonType.values()));
		
		//assign character to players randomly using Collection.shuffle
		for (int i = 0; i < players; i++) {
			Collections.shuffle(characters);
			this.players.add(new Player(characters.get(0)));
			characters.remove(0);
		}
		//tell the players what character they are
		int i = 1;
		for (Player p: this.players) {
			System.out.println("Player " + i + " is: " + p.toString());
			i++;
		}
		
		dealDeck();
		System.out.println("The envelope is:\n");
		for (Card c: envelope) {
			System.out.println(c.toString() + "\n");
		}
		for (Player p: this.players) {
			System.out.println(p.toString() + " has " + p.printHand());
		}
	}
	
	/**
	 * Helper method to get the number of players, and handles input errors
	 * 
	 * @param sc the scanner which reads in from the System.in standard input
	 * @return the number of people playing or -1 if there is a error
	 */
	public static int getNumberPlayers(Scanner sc) {
		//if there is something that has been typed
		if (sc.hasNext()) {
			try {
				//get the inputed string and try parsing it to a int
				int players = Integer.parseInt(sc.nextLine());
				//if the wrong number of player
				if (players < 3 || players > 6) {
					System.out.println("Please enter a number between 3 and 6:");
					return -1;
				}
				//else everything is correct, return the number of players
				else {
					return players;
				}
			}
			//catch parsing exception, if a digit is not inputed
			catch(Exception e){
				System.out.println("Please enter a integer number:");
				return -1;
			}
		}
		return 0;
	}
	
	public void dealDeck() {
		List<Card> personCards = new ArrayList<>();
		personCards.addAll(PersonCard.getAllCards());
		List<Card> weaponCards = new ArrayList<>();
		weaponCards.addAll(WeaponCard.getAllCards());
		List<Card> roomCards = new ArrayList<>();
		roomCards.addAll(RoomCard.getAllCards());
		
		Collections.shuffle(roomCards);
		Collections.shuffle(weaponCards);
		Collections.shuffle(personCards);
		
		envelope.add(roomCards.get(0));
		roomCards.remove(0);
		envelope.add(weaponCards.get(0));
		weaponCards.remove(0);
		envelope.add(personCards.get(0));
		personCards.remove(0);
		
		List<Card> deck = new ArrayList<>();
		deck.addAll(roomCards);
		deck.addAll(weaponCards);
		deck.addAll(personCards);
		Collections.shuffle(deck);
		int playerToDealTo = 0;
		
		for (Card c: deck) {
			players.get(playerToDealTo).giveCard(c);
			playerToDealTo++;
			if (playerToDealTo == players.size()) {
				playerToDealTo = 0;
			}
		}	
	}
}
