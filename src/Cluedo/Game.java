package Cluedo;

import Cluedo.Board.Board;
import Cluedo.Board.BoardFormatException;
import Cluedo.Board.BoardTile;
import Cluedo.Board.RoomTile;
import Cluedo.Card.Card;
import Cluedo.Card.PersonCard;
import Cluedo.Card.RoomCard;
import Cluedo.Card.WeaponCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class Game extends GUI{

	Set<Card> envelope = new HashSet<>();
	List<Player> players = new ArrayList<>();
	Map<WeaponCard.WeaponType, RoomCard.RoomType> weaponsInRoom = new HashMap<>();
	Board board;
	Player currentPlayer;
	int diceNumber1 = 0;
	int diceNumber2 = 0;

	RoomCard.RoomType hoveredRoom = null;

	@Override
	protected void drawBoard(Graphics g) {
		if (board != null) {
			board.draw(g);
		}
		if (hoveredRoom != null) {
			g.setColor(new Color(0x000000));
			g.fillRect(0, 480, 120, 20);
			g.setColor(new Color(0xFFFFFF));
			g.drawString(hoveredRoom.toString().replace('_', ' '), 5, 495);
		}
	}

	@Override
	protected void doMouseMoved(MouseEvent e) {
		if (board == null)
			return;
		if (e.getX() <= 10 || e.getX() >= 490 ||
				e.getY() <= 10 || e.getY() >= 490)
			return;
		Position position = new Position((e.getY() - 10) / 20, (e.getX() - 10) / 20);
		BoardTile tile = board.getTileAt(position);
		if (tile instanceof RoomTile) {
			if (hoveredRoom != ((RoomTile) tile).getRoom()) {
				hoveredRoom = ((RoomTile) tile).getRoom();
				boardGraphics.updateUI();
			}
		}
	}

	@Override
	protected void drawCards(Graphics g) {
		int xPos = 200;
		if (currentPlayer != null) {
			g.setColor(new Color(0x060606));
			g.fillRect(0, 0, 1150, 200);
			g.setColor(new Color (0x75525D));
			for (Card c : currentPlayer.hand) {
				c.draw(g, xPos, 10);
				xPos += 140;
			}
		}
	}

	@Override
	protected void drawDice(Graphics g) {
		drawSingleDice(g, 10, 10, diceNumber1);
		drawSingleDice(g, 70, 10, diceNumber2);
	}


	public void drawSingleDice(Graphics g, int xPos, int yPos, int value) {
		g.setColor(new Color(0x75525D));
		g.fillRoundRect(xPos, yPos, 50, 50, 10, 10);
		g.setColor(new Color(0x664751));
		g.drawRoundRect(xPos, yPos, 50, 50, 10, 10);
		g.setColor(Color.BLACK);
		if (value == 1) {
			g.drawOval(xPos + 22, yPos + 22, 6, 6);
		}
		if (value == 2) {
			g.drawOval(xPos + 39, yPos + 39, 6, 6);
			g.drawOval(xPos + 5, yPos + 5, 6, 6);
		}
		if (value == 3) {
			g.drawOval(xPos + 39, yPos + 39, 6, 6);
			g.drawOval(xPos + 5, yPos + 5, 6, 6);
			g.drawOval(xPos + 22, yPos + 22, 6, 6);
		}
		if (value == 4) {
			g.drawOval(xPos + 39, yPos + 39, 6, 6);
			g.drawOval(xPos + 5, yPos + 5, 6, 6);
			g.drawOval(xPos + 5, yPos + 39, 6, 6);
			g.drawOval(xPos + 39, yPos + 5, 6, 6);
		}
		if (value == 5) {
			g.drawOval(xPos + 39, yPos + 39, 6, 6);
			g.drawOval(xPos + 5, yPos + 5, 6, 6);
			g.drawOval(xPos + 5, yPos + 39, 6, 6);
			g.drawOval(xPos + 39, yPos + 5, 6, 6);
			g.drawOval(xPos + 22, yPos + 22, 6, 6);
		}
		if (value == 6) {
			g.drawOval(xPos + 39, yPos + 39, 6, 6);
			g.drawOval(xPos + 5, yPos + 5, 6, 6);
			g.drawOval(xPos + 5, yPos + 39, 6, 6);
			g.drawOval(xPos + 39, yPos + 5, 6, 6);
			g.drawOval(xPos + 5, yPos + 22, 6, 6);
			g.drawOval(xPos + 39, yPos + 22, 6, 6);
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.initialise();
		game.play();
	}

	/**
	 * Deals with general playing mechanics until the game is over
	 */
	public void play() {
		textArea.setText("");
		textArea.append("*************************************************\n"
                +"CLUEDO\n"
				+"*************************************************\n");
		int roundNumber = 1;
		boolean ready;
		gameLoop:
		while (true) {
			textArea.append("Round " + roundNumber + " starting!\n");
			int playerNumber = 1;
			int numPlayersLeft = 0;
			for (Player p : players) {
				boardGraphics.updateUI();
				currentPlayer = p;
				drawCards(cardGraphics.getGraphics());
				if (p.hasLost) {
					playerNumber++;
					continue;
				}
				System.out.println(board);
				//System.out.println("Player " + playerNumber + "'s turn! (" + p.personType.toString() + ") Rolling dice...");
				textArea.append(p.personType.toString() +"'s turn!\n");
				diceNumber1 = Turn.rollDice();
				diceNumber2 = Turn.rollDice();
				drawDice(cardGraphics.getGraphics());
				System.out.println("You have to take " + (diceNumber1 + diceNumber2) + " moves. What do you want to do?");

				//get input from player
				left.setVisible(true);
				right.setVisible(true);
				down.setVisible(true);
				up.setVisible(true);

				int movesTaken = 0;
				doMoves(movesTaken, diceNumber1 + diceNumber2, p, boardGraphics.getGraphics());

				left.setVisible(false);
				right.setVisible(false);
				down.setVisible(false);
				up.setVisible(false);

				RoomCard.RoomType r = board.getPlayerRoom(p);
				Turn t = new Turn(players);
				if (r != null) {
					textArea.append("You have entered the " + r.toString()+".\n");
					textArea.append("The weapons in this room are: \n");
					for (Map.Entry<WeaponCard.WeaponType, RoomCard.RoomType> e: weaponsInRoom.entrySet()){
						if (e.getValue() == r){
							System.out.println(e.getKey());
						}
					}
					textArea.append("Would you like to make a suggestion?\n");
					boolean suggest = yesOrNo();
					if (suggest) {
						Suggestion suggestion = t.makeSuggestion(r, false, this);
						clearSelections();
						weaponsInRoom.remove(suggestion.weapon);
						weaponsInRoom.put(suggestion.weapon, suggestion.room);
						Player inSuggestion = this.getPlayerFromType(suggestion.person);
						if (inSuggestion != null) {
							board.movePlayerToPlayer(p, inSuggestion);
						}
						t.disproveSuggestion(p, suggestion, this);
					}
						textArea.append("Would you now like to make an accusation?\n" 
					                    + " This is will be your final guess\n");
						boolean accuse = yesOrNo();
						if (accuse) {
							Suggestion accusation = t.makeSuggestion(r, true, this);
							boolean win = t.accusationCheck(p, accusation, envelope);
							clearSelections();
							if (win) {
								textArea.setText("");
								textArea.append("*************************************************\n"
						                +"CLUEDO\n"
										+"*************************************************\n");
								System.out.println("Player " + playerNumber + " has solved the murder, and wins the game!");
								textArea.append(p.personType.toString()+" wins!!\n");
								break gameLoop;
							} else {
								System.out.println("Player " + playerNumber + " has guessed incorrectly. They are now out of the game.");
								textArea.append(p.personType.toString()+" is out of the game\n");
								p.hasLost = true;
								board.killPlayer(p);
							}
						}

				}
				playerNumber++;
			}
			for (Player p: players){
				if (!p.hasLost){
					numPlayersLeft++;
				}
			}
			textArea.setText("");
			textArea.append("*************************************************\n"
	                +"CLUEDO\n"
					+"*************************************************\n");
			if (numPlayersLeft == 1){
				//System.out.println("Only one player left. Game Over");
				textArea.append("Game Over\n");
				break gameLoop;
			}
			//System.out.println("Round " + roundNumber + " finished! Ready for the next round?\n");
			textArea.append("Round " + roundNumber + " finished!\n");
			//wait for the players to be ready for the next round
			ready = false;
			do {
				ready = doReady();
			}
			while (!ready);
			this.ready.setVisible(false);
			roundNumber++;
		}
	}


	/**
	 * This method initializes the state of the game, creating players, their hands,
	 * the envelope and the board
	 */

	public void initialise() {
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int quitting = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Warning", JOptionPane.YES_NO_OPTION);
				if (quitting == JOptionPane.YES_OPTION){
					window.dispose();
				}
			}
		});
		textArea.setBackground(Color.black);
		textArea.setFont(new Font("Dialog", Font.BOLD, 15));
		textArea.setForeground(Color.magenta);
		textArea.append("*************************************************\n"
		                +"CLUEDO\n");
		textArea.append("*************************************************\n"
						 + "Welcome to Cluedo!\n" 
				         + "How many people are playing?\n");

		// get the number of players playing
		int players;

		do {
			players = getPlayerNumbers(); //GUI method that allows user to click buttons
		} while (players == -1);

		//let players select characters
		int index = 1;
		while (this.players.size() < players){
			textArea.append("Player " + index + ": choose a character to play \n");
			PersonCard.PersonType selected = chooseChar();
			this.players.add(new Player(selected));
			index++;
		}
		for (JRadioButton button : people.values()){
			button.setVisible(false);
		}

		textArea.append("Ready to shuffle and deal?\n");
		
		//waits for the player to say they are ready
		boolean ready;
		do {
			ready = doReady();
		} while (!ready);

		this.ready.setVisible(false);
		
		//deal cards and displays information to the players
		dealDeck();

		for (Player p : this.players) {
			System.out.println(p.toString() + " has the cards:\n" + p.handToString());
		}

		//create the board
		try {
			board = new Board(Board.DEFAULT_BOARD, this.players);
		} catch (BoardFormatException e) {
			System.out.println("Oops!\n" + e.getCause());
		}

		List<WeaponCard.WeaponType> weapons = new ArrayList<>(Arrays.asList(WeaponCard.WeaponType.values()));
		List<RoomCard.RoomType> rooms = new ArrayList<>(Arrays.asList(RoomCard.RoomType.values()));
		Collections.shuffle(rooms);
		int roomToPut = 0;
		for (WeaponCard.WeaponType w: weapons){
			weaponsInRoom.put(w, rooms.get(roomToPut));
			roomToPut++;
		}

		for (Map.Entry<WeaponCard.WeaponType, RoomCard.RoomType> e: weaponsInRoom.entrySet()){
			System.out.println("The weapon " + e.getKey() + " is in the room " + e.getValue());
		}
 
		textArea.append("Everything is ready! Ready to start?\n");
		
		//waits for the player to say they are ready
		Boolean sReady = false;
		do {
			sReady = doReady();
		} while (!sReady);
		this.ready.setVisible(false);
	}

	public PersonCard.PersonType chooseChar(){
		final PersonCard.PersonType[] selected = new PersonCard.PersonType[1];
		for (JRadioButton p : people.values()){
			p.setVisible(true);
		}
		while (selected[0] == null) {
			for (PersonCard.PersonType pt : people.keySet()) {
				JRadioButton button = people.get(pt);
				button.addActionListener(e -> {
					button.setEnabled(false);
					button.setSelected(true);
					selected[0] = pt;
				});
			}
		}
		return selected[0];
	}

	/**
	 * New getPlayerNumber method to select number of players: uses buttons
	 */
	private int plSelected = -1;
	public int getPlayerNumbers(){
		for (JButton b: playerNumbers){
			b.setVisible(true);
		}

		for (JButton b : playerNumbers){
			b.addActionListener(e -> {
				plSelected = 3 + playerNumbers.indexOf(b);
				for (JButton button : playerNumbers){
					button.setVisible(false);
				}
			});
		}
		return plSelected;
	}

	/**
	 * New yes/no method: uses buttons
	 */
	private boolean answer;
	public boolean yesOrNo(){
		final boolean[] answered = new boolean[1]; //checks if an answer has been selected
		while (!answered[0]) {
			yes.setVisible(true);
			no.setVisible(true);
			yes.addActionListener(e -> {
				answered[0] = true;
				answer = true;
				yes.setVisible(false);
				no.setVisible(false);
			});
			no.addActionListener(e -> {
				answered[0] = true;
				answer = false;
				yes.setVisible(false);
				no.setVisible(false);
			});
		}
		return answer;
	}

	public boolean doReady(){
		final boolean[] bReady = new boolean[1];
		ready.setVisible(true);
		while (!bReady[0]) {
			ready.addActionListener(e -> bReady[0] = true);
		}
		return bReady[0];
	}

	/**
	 *
	 * @param movesTaken the number of moves taken so far
	 * @param diceNumber the number rolled
	 * @param p player
	 */
	public void doMoves(int movesTaken, int diceNumber, Player p, Graphics g){
		boolean first = true;
		while (movesTaken < diceNumber) {
			ActionListener listener = e -> {
				JButton button = (JButton) e.getSource();
				if (button == left) {
					mLeft = true;
				}
				if (button == right) {
					mRight = true;
				}
				if (button == up) {
					mUp = true;
				}
				if (button == down) {
					mDown = true;
				}
			};

			if (first) {
				left.addActionListener(listener);
				right.addActionListener(listener);
				down.addActionListener(listener);
				up.addActionListener(listener);
			}

			if (mLeft) {
				if (board.movePlayer("l", 2, p)) {
					drawBoard(g);
					movesTaken++;
				}
				mLeft = false;
			} else if (mRight) {
				if (board.movePlayer("r", 2, p)) {
					drawBoard(g);
					movesTaken++;
				}
				mRight = false;
			} else if (mUp) {
				if (board.movePlayer("u", 2, p)) {
					drawBoard(g);
					movesTaken++;
				}
				mUp = false;
			} else if (mDown) {
				if (board.movePlayer("d", 2, p)) {
					drawBoard(g);
					movesTaken++;
				}
				mDown = false;
			}
			first = false;
			if (movesTaken == diceNumber - 1){ //if last loop
				left.removeActionListener(listener);
				right.removeActionListener(listener);
				up.removeActionListener(listener);
				down.removeActionListener(listener);
			}
		}
	}

	public void clearSelections(){
		for (JButton button : characters.values()) {
			button.setBackground(new Color(0x1f1d1d));
			button.setVisible(false);
		}
		for (JButton button : weapons.values()) {
			button.setBackground(new Color(0x1f1d1d));
			button.setVisible(false);
		}
		for (JButton button : rooms.values()){
			button.setBackground(new Color(0x1f1d1d));
			button.setVisible(false);
		}
	}

	/**
	 * gets a player from a specific playerType
	 * @param p the personType player to find
	 * @return the player with the given personType, null if not in game
	 */
	private Player getPlayerFromType(PersonCard.PersonType p){
		for (Player player: players){
			if (player.personType == p){
				return player;
			}
		}
		return null;
	}

	/**
	 * Helper method that deal with dealing out the hands to the players and making
	 * the envelope
	 */
	public void dealDeck() {
		// get each type of card
		List<Card> personCards = new ArrayList<>(PersonCard.getAllCards());
		List<Card> weaponCards = new ArrayList<>(WeaponCard.getAllCards());
		List<Card> roomCards = new ArrayList<>(RoomCard.getAllCards());

		Collections.shuffle(roomCards);
		Collections.shuffle(weaponCards);
		Collections.shuffle(personCards);

		// create envelope
		envelope.add(roomCards.get(0));
		roomCards.remove(0);
		envelope.add(weaponCards.get(0));
		weaponCards.remove(0);
		envelope.add(personCards.get(0));
		personCards.remove(0);

		// add the rest to the deck, ready to be dealt
		List<Card> deck = new ArrayList<>();
		deck.addAll(roomCards);
		deck.addAll(weaponCards);
		deck.addAll(personCards);
		Collections.shuffle(deck);
		int playerToDealTo = 0;

		// deal the cards to each player
		for (Card c : deck) {
			players.get(playerToDealTo).giveCard(c);
			playerToDealTo++;
			if (playerToDealTo == players.size()) {
				playerToDealTo = 0;
			}
		}
	}

}
