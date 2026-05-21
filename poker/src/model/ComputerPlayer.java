package model;

import java.util.Random;

/**
 * ComputerPlayer represents the computer opponent in the Poker game.
 * It contains simple probabilistic logic to automate decisions (Check, Call, Fold, Raise).
 */
public class ComputerPlayer extends Player {
    private final Random random;

    public ComputerPlayer(String name, int initialChips) {
        super(name, initialChips);
        this.random = new Random();
    }

    /**
     * Automatic simple decision-making for the computer opponent.
     * Depending on the current highest bet, the bot will randomly choose:
     * - Check (if no active bets to call)
     * - Call (matching the highest bet)
     * - Fold (giving up the hand)
     * - Raise (increasing the highest bet by a small amount)
     *
     * This method automatically executes the chips deduction and state changes.
     *
     * @param highestBet The current highest bet in the active betting round.
     * @return A String describing the action taken by the computer.
     */
    public String makeDecision(int highestBet) {
        if (isFolded()) {
            return "FOLD (Already Folded)";
        }

        int toCall = highestBet - getCurrentBet();

        if (toCall <= 0) {
            // Nothing to call -> options: CHECK or RAISE
            int roll = random.nextInt(100);
            if (roll < 20 && getChips() >= 10) {
                // 20% chance to Raise
                int raiseAmount = (random.nextInt(3) + 1) * 10; // Raise by 10, 20, or 30
                raiseAmount = Math.min(raiseAmount, getChips());
                bet(raiseAmount);
                return "RAISE to " + getCurrentBet() + " (raised by " + raiseAmount + ")";
            } else {
                // 80% chance to Check
                return "CHECK";
            }
        } else {
            // Active bet to call -> options: FOLD, CALL, or RAISE
            int roll = random.nextInt(100);
            if (roll < 20) {
                // 20% chance to Fold
                fold();
                return "FOLD";
            } else if (roll < 35 && getChips() > (toCall + 10)) {
                // 15% chance to Raise (calling the bet and adding a small raise)
                int raiseAmount = (random.nextInt(2) + 1) * 10; // Raise by 10 or 20
                raiseAmount = Math.min(raiseAmount, getChips() - toCall);
                bet(toCall + raiseAmount);
                return "RAISE to " + getCurrentBet() + " (raised by " + raiseAmount + ")";
            } else {
                // 65% chance to Call
                if (getChips() >= toCall) {
                    bet(toCall);
                    return "CALL";
                } else {
                    // Cannot afford to call, must fold
                    fold();
                    return "FOLD";
                }
            }
        }
    }
}
