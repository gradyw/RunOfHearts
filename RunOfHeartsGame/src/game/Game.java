package game;

import java.util.*;

/**
 * "Run of Hearts"
 *
 * Play the card game Hearts, with a couple extra twists.
 * 
 * @author Grady Whelan
 */
public class Game {

	private static ArrayList<Player> players = new ArrayList<>();
	private static int numberOfPlayers;
	private static Player leadingPlayer;
	private static ArrayList<Card> deck = new ArrayList<>();

	static Scanner scanner = new Scanner(System.in);
	static HashMap<Player, Card> cardsInPlay = new HashMap<>();
	static Suit suitLed;
	static boolean heartsBroken = false;

	
	/**
	 * Card enum contains all of the cards in the deck, with parameters suit, number value, and name
	 */
	enum Card {
		C2(Suit.CLUBS, 2, "C2"), C3(Suit.CLUBS, 3, "C3"), C4(Suit.CLUBS, 4, "C4"), C5(Suit.CLUBS, 5, "C5"),
		C6(Suit.CLUBS, 6, "C6"), C7(Suit.CLUBS, 7, "C7"), C8(Suit.CLUBS, 8, "C8"), C9(Suit.CLUBS, 9, "C9"),
		C10(Suit.CLUBS, 10, "C10"), CJ(Suit.CLUBS, 11, "CJ"), CQ(Suit.CLUBS, 12, "CQ"), CK(Suit.CLUBS, 13, "CK"),
		CA(Suit.CLUBS, 13, "CA"), D2(Suit.DIAMONDS, 2, "D2"), D3(Suit.DIAMONDS, 3, "D3"), D4(Suit.DIAMONDS, 4, "D4"),
		D5(Suit.DIAMONDS, 5, "D5"), D6(Suit.DIAMONDS, 6, "D6"), D7(Suit.DIAMONDS, 7, "D7"), D8(Suit.DIAMONDS, 8, "D8"),
		D9(Suit.DIAMONDS, 9, "D9"), D10(Suit.DIAMONDS, 10, "D10"), DJ(Suit.DIAMONDS, 11, "DJ"),
		DQ(Suit.DIAMONDS, 12, "DQ"), DK(Suit.DIAMONDS, 13, "DK"), DA(Suit.DIAMONDS, 13, "DA"), H2(Suit.HEARTS, 2, "H2"),
		H3(Suit.HEARTS, 3, "H3"), H4(Suit.HEARTS, 4, "H4"), H5(Suit.HEARTS, 5, "H5"), H6(Suit.HEARTS, 6, "H6"),
		H7(Suit.HEARTS, 7, "H7"), H8(Suit.HEARTS, 8, "H8"), H9(Suit.HEARTS, 9, "H9"), H10(Suit.HEARTS, 10, "H10"),
		HJ(Suit.HEARTS, 11, "HJ"), HQ(Suit.HEARTS, 12, "HQ"), HK(Suit.HEARTS, 13, "HK"), HA(Suit.HEARTS, 13, "HA"),
		S2(Suit.SPADES, 2, "S2"), S3(Suit.SPADES, 3, "S3"), S4(Suit.SPADES, 4, "S4"), S5(Suit.SPADES, 5, "S5"),
		S6(Suit.SPADES, 6, "S6"), S7(Suit.SPADES, 7, "S7"), S8(Suit.SPADES, 8, "S8"), S9(Suit.SPADES, 9, "S9"),
		S10(Suit.SPADES, 10, "S10"), SJ(Suit.SPADES, 11, "SJ"), SQ(Suit.SPADES, 12, "SQ"), SK(Suit.SPADES, 13, "SK"),
		SA(Suit.SPADES, 13, "SA");

		Suit suit;
		int value;
		String name;

		/**
		 * A card in the deck
		 * @param suit of the card
		 * @param value numerical of the card
		 * @param name of the card
		 */
		Card(Suit suit, int value, String name) {
			this.suit = suit;
			this.value = value;
			this.name = name;
		}
	}

	public static void main(String[] args) throws Exception {
		boolean playAgain = true;
		while (playAgain) {
			players.clear();
			deck.clear();
//			System.out.println(deck);
			System.out.print("What is the maximum score? ");
			int maxScore = scanner.nextInt();
			System.out.print("How many players are playing? ");
			numberOfPlayers = scanner.nextInt();
			for (int playerNumber = 1; playerNumber < numberOfPlayers + 1; playerNumber++) {
				System.out.print("Enter Player " + playerNumber + " Name: ");
				String name = scanner.next();
				players.add(new Player(name));
			}

			// below to loop each round
			for (int roundNumber = 1; getLosingPlayer().pointsTotal < maxScore; roundNumber++) {

				System.out.println("Round " + roundNumber);

				createDeck();
				int cardsPerPlayer = 52 / numberOfPlayers;
				int cardsRemaining = 52 - cardsPerPlayer * numberOfPlayers;
				for (Player player : players) {
					for (int i = 1; i < cardsPerPlayer + 1; i++) {
						if (deck.size() <= 0) {
							// this should never run
							break;
						}
						Random random = new Random();
						int randomIndex = random.nextInt(deck.size());
						player.hand.add(deck.get(randomIndex));
						deck.remove(deck.get(randomIndex));
					}

					if (player.hand.size() != cardsPerPlayer)
						throw new Exception();
				}
				if (deck.size() != cardsRemaining)
					throw new Exception();

				// AT THIS POINT, cards have been dealt.

				System.out.println("There are " + cardsRemaining
						+ " cards that will be revealed after and given to the winner of the first trick.");

				leadingPlayer = playC2();
				boolean firstTrick = true;

				while (leadingPlayer.hand.size() > 0) {
					if (!firstTrick) {
						System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" // newlines hide
								+ leadingPlayer.name + " leads!");										// previous trick
						leadingPlayer.takeTurn(true);
					}

					Player currentPlayer = leadingPlayer;

					suitLed = cardsInPlay.get(leadingPlayer).suit;

					// below to loop each trick
					while (cardsInPlay.size() < numberOfPlayers) {
						System.out.println(suitLed + " was led.");
						currentPlayer = getNextPlayer(currentPlayer);
						currentPlayer.takeTurn(false);
					}

					if (firstTrick) {

						Player nonPlayer = new Player("Extra");

						for (Card extraCard : deck) {
							cardsInPlay.put(nonPlayer, extraCard);
							System.out.print(extraCard.name + " ");
						}
						System.out.println();
						firstTrick = false;
					}

					Player winningPlayer = getWhoWonTrick();

					winningPlayer.takeTrick(cardsInPlay.values());

					leadingPlayer = winningPlayer;
					cardsInPlay.clear();
				}

				// End of Round

				System.out.println("Round " + roundNumber + " Scores");
				for (Player player : players) {
					System.out.println(player.name + " | " + player.pointsRound);
					player.roundEnd();
				}

				System.out.println("Total Scores");
				for (Player player : players) {
					System.out.println(player.name + " | " + player.pointsTotal);
				}
			}

			System.out.print("Play again? (Y/N): ");
			String again = scanner.next();
			playAgain = again.toUpperCase().equals("Y");

		}
		scanner.close();
	}

	private static void createDeck() {
		deck.clear();
		deck.addAll(Arrays.asList(Card.values()));
	}

	/**
	 * plays the C2 from player that has it
	 * 
	 * @return index in players of that player
	 */
	private static Player playC2() throws Exception {
		for (Player player : players) {
			for (Card card : player.hand) {
				if (card == Card.C2) {
					System.out.println(player.name + " has played the C2");
					cardsInPlay.put(player, card);
					player.hand.remove(card);
					return player;
				}
			}
		}
		return players.get(0);
	}

	/**
	 * Compares scores of all players to find losing player
	 * @return player with highest score
	 */
	private static Player getLosingPlayer() {
		int highScore = 0;
		Player losingPlayer = players.get(0);
		for (Player player : players) {
			if (player.pointsTotal > highScore) {
				highScore = player.pointsTotal;
				losingPlayer = player;
			}
		}
		return losingPlayer;
	}

	/**
	 * Compares suits of cards in trick to suit led and compares values of
	 * those who followed suit to find who won the trick
	 * @return player who won the trick with highest card of suit led
	 */
	private static Player getWhoWonTrick() {
		Suit suitLed = cardsInPlay.get(leadingPlayer).suit;
		Card bestCard = cardsInPlay.get(leadingPlayer);
		Player bestPlayer = leadingPlayer;
		for (Player player : cardsInPlay.keySet()) {
			if (players.contains(player) && cardsInPlay.get(player).suit == suitLed
					&& cardsInPlay.get(player).value > bestCard.value) {
				bestCard = cardsInPlay.get(player);
				bestPlayer = player;
			}
		}

		return bestPlayer;
	}

	/**
	 * The 4 suits in a card deck
	 */
	enum Suit {
		CLUBS, DIAMONDS, HEARTS, SPADES
	}

	/**
	 * Finds next player in line based on current player.
	 * @param currentPlayer
	 * @return the next player
	 */
	private static Player getNextPlayer(Player currentPlayer) {
		if (players.indexOf(currentPlayer) == numberOfPlayers - 1)
			return players.get(0);
		else
			return players.get(players.indexOf(currentPlayer) + 1);
	}

	/**
	 * Print the cards played in a trick so far in a formatted
	 * way that is easy to see who played what (for the next player(s)).
	 */
	static void printFormattedTrick() {
		if (!cardsInPlay.isEmpty())
			System.out.println("Trick: " + suitLed);
		for (Player player : cardsInPlay.keySet()) {
			System.out.println(player.name + " | " + cardsInPlay.get(player));
		}
	}
}


