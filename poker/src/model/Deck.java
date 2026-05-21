package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Deck represents a standard deck of 52 playing cards.
 * It provides methods to shuffle, deal cards, and reset the deck.
 */
public class Deck {
    private final List<Card> cards;
    private int nextCardIndex;

    public Deck() {
        this.cards = new ArrayList<>(52);
        reset();
    }

    /**
     * Initializes the deck with 52 standard cards.
     * Resets the dealing index.
     */
    public void reset() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        nextCardIndex = 0;
    }

    /**
     * Shuffles the cards randomly and resets the dealing pointer.
     */
    public void shuffle() {
        Collections.shuffle(cards);
        nextCardIndex = 0;
    }

    /**
     * Deals a single card from the deck.
     * @return The next Card, or null if the deck is exhausted.
     */
    public Card dealCard() {
        if (nextCardIndex >= cards.size()) {
            return null;
        }
        return cards.get(nextCardIndex++);
    }

    /**
     * Gets the number of remaining undealt cards.
     */
    public int getCardsRemaining() {
        return cards.size() - nextCardIndex;
    }

    /**
     * Gets a copy of all the cards currently in the deck list.
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}
