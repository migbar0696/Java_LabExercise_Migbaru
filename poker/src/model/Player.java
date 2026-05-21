package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Player represents a poker player (either human or computer).
 * It holds the player's status: hand cards, chip count, active bet, and folded state.
 */
public class Player {
    private final String name;
    private int chips;
    private final List<Card> hand;
    private boolean folded;
    private int currentBet;

    public Player(String name, int initialChips) {
        this.name = name;
        this.chips = initialChips;
        this.hand = new ArrayList<>(2);
        this.folded = false;
        this.currentBet = 0;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        if (amount > 0) {
            this.chips += amount;
        }
    }

    public List<Card> getHand() {
        return hand;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void clearBet() {
        this.currentBet = 0;
    }

    // --- Actions ---

    /**
     * Adds a hole card to the player's hand.
     */
    public void addCard(Card card) {
        if (card != null && hand.size() < 2) {
            hand.add(card);
        }
    }

    /**
     * Clears all cards in the player's hand.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Sets the player's state to folded.
     */
    public void fold() {
        this.folded = true;
    }

    /**
     * Places a bet. Deducts chips and adds to current bet.
     * @param amount The chip amount to bet.
     */
    public void bet(int amount) {
        if (amount < 0) return;
        if (amount > chips) {
            amount = chips; // Cap at all-in if called with too high amount
        }
        chips -= amount;
        currentBet += amount;
    }

    @Override
    public String toString() {
        return name + " (Chips: " + chips + ", Bet: " + currentBet + ", Hand: " + hand + (folded ? " [FOLDED]" : "") + ")";
    }
}
