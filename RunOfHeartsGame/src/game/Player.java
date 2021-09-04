package game;

import java.util.ArrayList;
import java.util.Collection;

import game.Game.Card;
import game.Game.Suit;


import static game.Game.cardsInPlay;
import static game.Game.scanner;

/**
 * The Player object provides methods for tracking of
 * each player's hand, tricks collected, and score.
 *
 * @author Grady Whelan
 */
class Player {

	String name;
	int pointsRound = 0;
	int pointsTotal = 0;
	ArrayList<Card> hand = new ArrayList<>();
	private ArrayList<Collection<Card>> tricks = new ArrayList<>();

	Player(String name) {
		this.name = name;
	}

	/**
	 * Play a card from player's hand
	 * @param leading whether this turn is one in which the player is leading
	 */
	void takeTurn(boolean leading) {
		boolean ready = false;
		while (!ready) {
			System.out.print("It's " + name + "'s turn! Type Y when ready: ");
			ready = scanner.next().toUpperCase().equals("Y");
		}
		Game.printFormattedTrick();
		System.out.println("\n" + name + " hand: " + hand);
		Card cardPlayed = getPlayedCard(leading);
		if (cardPlayed.suit == Suit.HEARTS)
			Game.heartsBroken = true;
		cardsInPlay.put(this, cardPlayed);
		hand.remove(cardPlayed);
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");// newlines hide the
																									// hand from next
																									// player
	}

	/**
	 * Take a trick and add points accordingly
	 * @param trick the trick won
	 */
	void takeTrick(Collection<Card> trick) {
		tricks.add(trick);
		System.out.println(name + " took the trick!");
		for (Card card : trick) {
			if (card.suit == Suit.HEARTS)
				pointsRound += 1;
			if (card.name.equals("SQ"))
				pointsRound += 13;
			if (card.name.equals("DJ"))
				pointsRound -= 11;
			for (Card testCard : trick) {
				if (testCard.value == card.value + 1) {
					for (Card testCard1 : trick) {
						if (testCard1.value == testCard.value +1) {
							pointsRound -= card.value;
							System.out.println(name + " got " + card.value + " negative point bonus for a run!");
						}
					}
				}
			}
		}
		Game.printFormattedTrick();
	}

	/**
	 * Add round points to running total at the end of the round
	 */
	void roundEnd() {
		pointsTotal += pointsRound;
		pointsRound = 0;
	}

	/**
	 * Ask the player what card they want to play, and ensure it follows suit.
	 * @param leading if the player is leading
	 * @return the card to be played
	 */
	private Card getPlayedCard(boolean leading) {
		System.out.print("What card would you like to play? ");
		boolean hasSuitLed = false;
		for (Card card : hand) {
			if (card.suit == Game.suitLed) {
				hasSuitLed = true;
				break;
			}
		}
		while (true) {
			String string = scanner.next();
			String playAgainExplanation = "You don't have that card.";
			for (Card card : hand) {
				if (string.toUpperCase().equals(card.name) && card.suit == Suit.HEARTS && leading
						&& !Game.heartsBroken) {
					playAgainExplanation = "You may not lead hearts as hearts have not been broken.";
					break;
				}
				if (string.toUpperCase().equals(card.name) && card.suit != Game.suitLed && !leading && hasSuitLed) {
					playAgainExplanation = "You must follow suit. " + Game.suitLed + " was led.";
					break;
				}
				if (string.toUpperCase().equals(card.name))
					return card;
			}
			System.out.print(playAgainExplanation + " Play another card: ");
		}
	}

}