package model;

/**
 * Card represents an individual playing card with a Rank and a Suit.
 */
public class Card {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    /**
     * Helper to check if card suit is red (Hearts or Diamonds).
     */
    public boolean isRed() {
        return suit == Suit.HEARTS || suit == Suit.DIAMONDS;
    }

    @Override
    public String toString() {
        return rank.toString() + suit.getShortSymbol();
    }
}
