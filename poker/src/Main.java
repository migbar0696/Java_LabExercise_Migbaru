import javax.swing.SwingUtilities;
import gui.PokerFrame;
import model.Deck;
import model.Card;
import model.Player;
import model.ComputerPlayer;
import game.GameState;
import game.HandEvaluator;
import game.HandEvaluator.HandScore;
import model.Suit;
import model.Rank;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point of the Poker game application.
 */
public class Main {
    public static void main(String[] args) {
        // If "test" argument is passed, run the console test for Phase 2
        if (args.length > 0 && "test".equalsIgnoreCase(args[0])) {
            runConsoleTest();
            return;
        }

        // Run GUI initialization on the Event Dispatch Thread (EDT) for safety
        SwingUtilities.invokeLater(() -> {
            GameState gameState = new GameState();
            PokerFrame frame = new PokerFrame(gameState);
            frame.setVisible(true);
        });
    }

    private static void runConsoleTest() {
        System.out.println("=== POKER GAME CONSOLE TEST (PHASE 2) ===");
        Deck deck = new Deck();
        
        System.out.println("Initial deck size (should be 52): " + deck.getCardsRemaining());
        System.out.println("Dealt first card (unshuffled): " + deck.dealCard());
        
        System.out.println("\nShuffling deck...");
        deck.shuffle();
        
        System.out.println("Dealt 5 cards after shuffle:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  Card " + (i + 1) + ": " + deck.dealCard());
        }
        
        System.out.println("\nRemaining cards in deck: " + deck.getCardsRemaining());
        System.out.println("Resetting deck...");
        deck.reset();
        System.out.println("Cards after reset (should be 52): " + deck.getCardsRemaining());
        
        System.out.println("\nPrinting full shuffled deck list:");
        deck.shuffle();
        int count = 0;
        for (Card card : deck.getCards()) {
            System.out.print(card + " ");
            count++;
            if (count % 13 == 0) {
                System.out.println();
            }
        }
        
        System.out.println("\n=== POKER GAME PLAYER & BOT TEST (PHASE 3) ===");
        deck.reset();
        deck.shuffle();
        
        // Create players with 1000 chips each
        Player player = new Player("Alice", 1000);
        ComputerPlayer bot = new ComputerPlayer("BotBob", 1000);
        
        // Deal 2 hole cards to each player
        player.addCard(deck.dealCard());
        player.addCard(deck.dealCard());
        
        bot.addCard(deck.dealCard());
        bot.addCard(deck.dealCard());
        
        System.out.println("Initial State (After dealing 2 hole cards):");
        System.out.println("  " + player);
        System.out.println("  " + bot);
        
        System.out.println("\n--- Simulation 1: Player checks (Highest bet is 0) ---");
        System.out.println("  Bot decision (when highest bet = 0):");
        String action1 = bot.makeDecision(0);
        System.out.println("  Bot Action: " + action1);
        System.out.println("  State after action:");
        System.out.println("    " + player);
        System.out.println("    " + bot);
        
        // Reset bet status and folded state for simulation 2
        player.clearBet();
        bot.clearBet();
        bot.setFolded(false);
        
        System.out.println("\n--- Simulation 2: Player bets 50 (Highest bet is 50) ---");
        player.bet(50);
        System.out.println("  State before bot acts:");
        System.out.println("    " + player);
        System.out.println("    " + bot);
        
        System.out.println("  Bot decision (when highest bet = 50):");
        String action2 = bot.makeDecision(50);
        System.out.println("  Bot Action: " + action2);
        System.out.println("  State after action:");
        System.out.println("    " + player);
        System.out.println("    " + bot);
        System.out.println("=========================================");
    }
}
