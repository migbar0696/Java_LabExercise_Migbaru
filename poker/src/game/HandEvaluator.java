package game;

import model.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HandEvaluator evaluates Texas Hold'em hands.
 * It takes a player's 2 hole cards and the 5 community cards (total 7 cards),
 * generates all 21 combinations of 5 cards, and determines the best possible hand.
 */
public class HandEvaluator {

    /**
     * Represents the score of a 5-card hand, consisting of a HandType and ordered kicker values.
     */
    public static class HandScore implements Comparable<HandScore> {
        public enum HandType {
            HIGH_CARD("High Card", 1),
            PAIR("Pair", 2),
            TWO_PAIR("Two Pair", 3),
            THREE_OF_A_KIND("Three of a Kind", 4),
            STRAIGHT("Straight", 5),
            FLUSH("Flush", 6),
            FULL_HOUSE("Full House", 7),
            FOUR_OF_A_KIND("Four of a Kind", 8),
            STRAIGHT_FLUSH("Straight Flush", 9);

            private final String name;
            private final int strength;

            HandType(String name, int strength) {
                this.name = name;
                this.strength = strength;
            }

            public String getName() {
                return name;
            }

            public int getStrength() {
                return strength;
            }
        }

        private final HandType type;
        private final List<Integer> kickerValues; // Ranks ordered by frequency then value for tie-breaking

        public HandScore(HandType type, List<Integer> kickerValues) {
            this.type = type;
            this.kickerValues = kickerValues;
        }

        public HandType getType() {
            return type;
        }

        public List<Integer> getKickerValues() {
            return kickerValues;
        }

        @Override
        public int compareTo(HandScore other) {
            // 1. Compare hand strength
            if (this.type.getStrength() != other.type.getStrength()) {
                return Integer.compare(this.type.getStrength(), other.type.getStrength());
            }
            // 2. Compare kicker values sequentially
            for (int i = 0; i < Math.min(this.kickerValues.size(), other.kickerValues.size()); i++) {
                int comp = Integer.compare(this.kickerValues.get(i), other.kickerValues.get(i));
                if (comp != 0) {
                    return comp;
                }
            }
            return 0; // Absolute tie
        }

        @Override
        public String toString() {
            return type.getName();
        }
    }

    /**
     * Evaluates the best 5-card combination out of 7 cards.
     * @param holeCards 2 player hole cards
     * @param communityCards 5 community cards
     * @return The best HandScore
     */
    public static HandScore evaluateBestHand(List<Card> holeCards, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(holeCards);
        allCards.addAll(communityCards);

        if (allCards.size() < 5) {
            return new HandScore(HandScore.HandType.HIGH_CARD, Collections.singletonList(0));
        }

        // Generate all 21 combinations of 5 cards from 7 cards
        List<List<Card>> combinations = new ArrayList<>();
        for (int i = 0; i < allCards.size(); i++) {
            for (int j = i + 1; j < allCards.size(); j++) {
                List<Card> combo = new ArrayList<>();
                for (int k = 0; k < allCards.size(); k++) {
                    if (k != i && k != j) {
                        combo.add(allCards.get(k));
                    }
                }
                combinations.add(combo);
            }
        }

        HandScore bestScore = null;
        for (List<Card> combo : combinations) {
            HandScore score = evaluate5CardHand(combo);
            if (bestScore == null || score.compareTo(bestScore) > 0) {
                bestScore = score;
            }
        }

        return bestScore;
    }

    /**
     * Evaluates a single 5-card hand.
     */
    public static HandScore evaluate5CardHand(List<Card> cards) {
        // Sort cards descending by rank value
        cards.sort((c1, c2) -> Integer.compare(c2.getRank().getValue(), c1.getRank().getValue()));

        // Check Flush
        boolean isFlush = true;
        for (int i = 1; i < 5; i++) {
            if (cards.get(i).getSuit() != cards.get(0).getSuit()) {
                isFlush = false;
                break;
            }
        }

        // Check Straight
        boolean isStraight = false;
        int highestStraightCardVal = cards.get(0).getRank().getValue();
        
        if (cards.get(0).getRank().getValue() - cards.get(4).getRank().getValue() == 4) {
            isStraight = true;
            for (int i = 0; i < 4; i++) {
                if (cards.get(i).getRank().getValue() - cards.get(i + 1).getRank().getValue() != 1) {
                    isStraight = false;
                    break;
                }
            }
        } else if (cards.get(0).getRank().getValue() == 14 && // Ace-5-4-3-2 straight
                   cards.get(1).getRank().getValue() == 5 &&
                   cards.get(2).getRank().getValue() == 4 &&
                   cards.get(3).getRank().getValue() == 3 &&
                   cards.get(4).getRank().getValue() == 2) {
            isStraight = true;
            highestStraightCardVal = 5; // A-5-4-3-2 has 5 as high straight card
        }

        // Group ranks by frequency to classify other hands
        int[] counts = new int[15];
        for (Card card : cards) {
            counts[card.getRank().getValue()]++;
        }

        class RankGroup implements Comparable<RankGroup> {
            final int rank;
            final int count;

            RankGroup(int rank, int count) {
                this.rank = rank;
                this.count = count;
            }

            @Override
            public int compareTo(RankGroup other) {
                if (this.count != other.count) {
                    return Integer.compare(other.count, this.count); // High frequency first
                }
                return Integer.compare(other.rank, this.rank); // High rank first
            }
        }

        List<RankGroup> groups = new ArrayList<>();
        for (int r = 2; r <= 14; r++) {
            if (counts[r] > 0) {
                groups.add(new RankGroup(r, counts[r]));
            }
        }
        Collections.sort(groups);

        // Map Hand Types
        List<Integer> kickers = new ArrayList<>();
        for (RankGroup g : groups) {
            kickers.add(g.rank);
        }

        if (isFlush && isStraight) {
            List<Integer> strFlushKicker = Collections.singletonList(highestStraightCardVal);
            return new HandScore(HandScore.HandType.STRAIGHT_FLUSH, strFlushKicker);
        }

        if (groups.get(0).count == 4) {
            return new HandScore(HandScore.HandType.FOUR_OF_A_KIND, kickers);
        }

        if (groups.get(0).count == 3 && groups.get(1).count == 2) {
            return new HandScore(HandScore.HandType.FULL_HOUSE, kickers);
        }

        if (isFlush) {
            return new HandScore(HandScore.HandType.FLUSH, kickers);
        }

        if (isStraight) {
            List<Integer> strKicker = Collections.singletonList(highestStraightCardVal);
            return new HandScore(HandScore.HandType.STRAIGHT, strKicker);
        }

        if (groups.get(0).count == 3) {
            return new HandScore(HandScore.HandType.THREE_OF_A_KIND, kickers);
        }

        if (groups.get(0).count == 2 && groups.get(1).count == 2) {
            return new HandScore(HandScore.HandType.TWO_PAIR, kickers);
        }

        if (groups.get(0).count == 2) {
            return new HandScore(HandScore.HandType.PAIR, kickers);
        }

        return new HandScore(HandScore.HandType.HIGH_CARD, kickers);
    }
}
