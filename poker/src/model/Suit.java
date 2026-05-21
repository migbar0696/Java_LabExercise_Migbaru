package model;

/**
 * Representing the four card suits in a standard deck of cards.
 */
public enum Suit {
    HEARTS("Hearts", "♥", "h"),
    DIAMONDS("Diamonds", "♦", "d"),
    CLUBS("Clubs", "♣", "c"),
    SPADES("Spades", "♠", "s");

    private final String displayName;
    private final String symbol;
    private final String shortSymbol;

    Suit(String displayName, String symbol, String shortSymbol) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.shortSymbol = shortSymbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getShortSymbol() {
        return shortSymbol;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
