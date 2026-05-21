package game;

import model.Deck;
import model.Card;
import model.Player;
import model.ComputerPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * GameState tracks the state of an active poker game.
 * It manages players, deck, community cards, pot size, active bet,
 * and handles transitions between game stages.
 */
public class GameState {
    
    public enum Stage {
        PRE_FLOP("Pre-Flop"),
        FLOP("Flop"),
        TURN("Turn"),
        RIVER("River"),
        SHOWDOWN("Showdown");

        private final String displayName;

        Stage(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Player player;
    private final ComputerPlayer computer;
    private final Deck deck;
    private final List<Card> communityCards;
    
    private Stage currentStage;
    private int pot;
    private int highestBet;
    
    public GameState() {
        this.player = new Player("Player (You)", 1000);
        this.computer = new ComputerPlayer("Computer (Bot)", 1000);
        this.deck = new Deck();
        this.communityCards = new ArrayList<>(5);
        startNewRound();
    }
    
    /**
     * Resets the cards, bets, and pot, then deals new hole cards.
     */
    public void startNewRound() {
        deck.reset();
        deck.shuffle();
        
        player.clearHand();
        player.clearBet();
        player.setFolded(false);
        
        computer.clearHand();
        computer.clearBet();
        computer.setFolded(false);
        
        communityCards.clear();
        currentStage = Stage.PRE_FLOP;
        
        // Deal 2 hole cards to each player
        player.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        
        computer.addCard(deck.dealCard());
        computer.addCard(deck.dealCard());
        
        // Post Blinds: Player is Small Blind (10), Computer is Big Blind (20)
        player.bet(10);
        computer.bet(20);
        pot = 30;
        highestBet = 20;
    }
    
    /**
     * Transitions the game to the next stage, dealing community cards as required.
     */
    public void nextStage() {
        // Reset betting round active bets
        player.clearBet();
        computer.clearBet();
        highestBet = 0;
        
        switch (currentStage) {
            case PRE_FLOP:
                currentStage = Stage.FLOP;
                // Deal 3 flop cards
                communityCards.add(deck.dealCard());
                communityCards.add(deck.dealCard());
                communityCards.add(deck.dealCard());
                break;
            case FLOP:
                currentStage = Stage.TURN;
                // Deal turn card
                communityCards.add(deck.dealCard());
                break;
            case TURN:
                currentStage = Stage.RIVER;
                // Deal river card
                communityCards.add(deck.dealCard());
                break;
            case RIVER:
                currentStage = Stage.SHOWDOWN;
                break;
            case SHOWDOWN:
                // No further transition
                break;
        }
    }
    
    // --- Getters & Setters ---

    public Player getPlayer() {
        return player;
    }

    public ComputerPlayer getComputer() {
        return computer;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public Stage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }

    public void addToPot(int amount) {
        this.pot += amount;
    }

    public int getHighestBet() {
        return highestBet;
    }

    public void setHighestBet(int highestBet) {
        this.highestBet = highestBet;
    }
    
    public boolean isRoundOver() {
        return currentStage == Stage.SHOWDOWN || player.isFolded() || computer.isFolded();
    }
}
