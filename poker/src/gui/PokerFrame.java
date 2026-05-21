package gui;

import game.GameState;
import game.HandEvaluator;
import model.Card;
import model.Player;
import model.ComputerPlayer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * PokerFrame represents the Texas Hold'em poker table window.
 * It manages betting actions and displays live chips, pot, and game progression.
 */
public class PokerFrame extends JFrame {
    
    private final GameState gameState;
    
    // UI Components
    private JLabel stageLabel;
    private JLabel potLabel;
    
    private JLabel computerChipsLabel;
    private JLabel computerBetLabel;
    private JPanel computerCardsPanel;
    
    private JLabel playerChipsLabel;
    private JLabel playerBetLabel;
    private JPanel playerCardsPanel;
    
    private JPanel communityCardsPanel;
    
    private JButton foldButton;
    private JButton checkButton;
    private JButton callButton;
    private JButton raiseButton;
    private JButton newRoundButton;
    
    // Table Colors
    private static final Color feltColor = new Color(27, 94, 32);     // #1b5e20
    private static final Color panelBgColor = new Color(38, 126, 45); // slightly lighter felt green
    private static final Color textColor = Color.WHITE;
    
    public PokerFrame(GameState gameState) {
        this.gameState = gameState;
        
        // Initialize basic window properties
        setTitle("Texas Hold'em Poker - Single Player");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main content pane with dark green felt background
        JPanel mainTable = new JPanel(new BorderLayout(10, 10));
        mainTable.setBackground(feltColor);
        mainTable.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainTable.setPreferredSize(new Dimension(980, 650));
        
        // Create individual areas with explicit sizes to prevent overlapping
        JPanel topPanel = createComputerArea();
        JPanel centerPanel = createCommunityArea();
        JPanel bottomPanel = createPlayerArea();
        
        // Add sections to the main board
        mainTable.add(topPanel, BorderLayout.NORTH);
        mainTable.add(centerPanel, BorderLayout.CENTER);
        mainTable.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainTable);
        
        // Force the frame size to prevent OS scaling/packing issues
        setPreferredSize(new Dimension(1000, 720));
        pack();
        setSize(1000, 720);
        setLocationRelativeTo(null);
        
        // Render initial game board state
        updateBoard();
    }
    
    /**
     * Creates the computer player area at the top of the window.
     */
    private JPanel createComputerArea() {
        JPanel panel = new JPanel(new BorderLayout(15, 5));
        panel.setBackground(panelBgColor);
        panel.setPreferredSize(new Dimension(950, 140));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), 
                        "Computer Opponent (Bot)", 
                        TitledBorder.LEFT, 
                        TitledBorder.TOP, 
                        new Font("Arial", Font.BOLD, 12), 
                        textColor
                ),
                new EmptyBorder(5, 15, 5, 15)
        ));
        
        // Stats sub-panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 5, 2));
        statsPanel.setBackground(panelBgColor);
        statsPanel.setPreferredSize(new Dimension(200, 80));
        
        computerChipsLabel = new JLabel();
        computerChipsLabel.setForeground(textColor);
        computerChipsLabel.setFont(new Font("Arial", Font.BOLD, 15));
        
        computerBetLabel = new JLabel();
        computerBetLabel.setForeground(new Color(255, 235, 59));
        computerBetLabel.setFont(new Font("Arial", Font.BOLD, 15));
        
        statsPanel.add(computerChipsLabel);
        statsPanel.add(computerBetLabel);
        
        // Cards sub-panel
        computerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        computerCardsPanel.setBackground(panelBgColor);
        
        panel.add(statsPanel, BorderLayout.WEST);
        panel.add(computerCardsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the community cards and pot area in the center of the window.
     */
    private JPanel createCommunityArea() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(feltColor);
        panel.setPreferredSize(new Dimension(950, 220));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Stage Label
        stageLabel = new JLabel("", JLabel.CENTER);
        stageLabel.setForeground(new Color(178, 235, 242));
        stageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(stageLabel, gbc);
        
        // Pot Label
        potLabel = new JLabel("", JLabel.CENTER);
        potLabel.setForeground(new Color(255, 235, 59));
        potLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridy = 1;
        panel.add(potLabel, gbc);
        
        // Community Cards Panel
        communityCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        communityCardsPanel.setBackground(panelBgColor);
        communityCardsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        communityCardsPanel.setPreferredSize(new Dimension(450, 115));
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        panel.add(communityCardsPanel, gbc);
        
        return panel;
    }
    
    /**
     * Creates the human player area and control buttons at the bottom.
     */
    private JPanel createPlayerArea() {
        JPanel panel = new JPanel(new BorderLayout(15, 10));
        panel.setBackground(panelBgColor);
        panel.setPreferredSize(new Dimension(950, 230));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), 
                        "Player (You)", 
                        TitledBorder.LEFT, 
                        TitledBorder.TOP, 
                        new Font("Arial", Font.BOLD, 12), 
                        textColor
                ),
                new EmptyBorder(5, 15, 5, 15)
        ));
        
        // Info sub-panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 2));
        infoPanel.setBackground(panelBgColor);
        infoPanel.setPreferredSize(new Dimension(200, 80));
        
        playerChipsLabel = new JLabel();
        playerChipsLabel.setForeground(textColor);
        playerChipsLabel.setFont(new Font("Arial", Font.BOLD, 15));
        
        playerBetLabel = new JLabel();
        playerBetLabel.setForeground(new Color(255, 235, 59));
        playerBetLabel.setFont(new Font("Arial", Font.BOLD, 15));
        
        infoPanel.add(playerChipsLabel);
        infoPanel.add(playerBetLabel);
        
        // Cards sub-panel
        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        playerCardsPanel.setBackground(panelBgColor);
        
        // Buttons Panel (Actions)
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        controls.setBackground(feltColor);
        controls.setBorder(new EmptyBorder(8, 10, 8, 10));
        controls.setPreferredSize(new Dimension(900, 50));
        
        foldButton = createStyledButton("Fold", new Color(183, 28, 28));
        checkButton = createStyledButton("Check", new Color(13, 71, 161));
        callButton = createStyledButton("Call", new Color(27, 94, 32));
        raiseButton = createStyledButton("Raise", new Color(230, 81, 0));
        newRoundButton = createStyledButton("New Round", new Color(74, 20, 140));
        
        controls.add(foldButton);
        controls.add(checkButton);
        controls.add(callButton);
        controls.add(raiseButton);
        controls.add(newRoundButton);
        
        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(playerCardsPanel, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        
        setupActionListeners();
        
        return panel;
    }
    
    /**
     * Styles standard action buttons.
     */
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Generates a face-up card UI component using HTML labels to support color suits.
     */
    private JLabel createCardComponent(Card card) {
        String rank = card.getRank().getSymbol();
        String suitSymbol = card.getSuit().getSymbol();
        boolean isRed = card.isRed();
        
        JLabel label = new JLabel("<html><center>" + rank + "<br><font size='+1'>" + suitSymbol + "</font></center></html>", JLabel.CENTER);
        label.setPreferredSize(new Dimension(65, 95));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        label.setFont(new Font("Arial", Font.BOLD, 18));
        
        if (isRed) {
            label.setForeground(new Color(198, 40, 40));
        } else {
            label.setForeground(Color.BLACK);
        }
        
        return label;
    }
    
    /**
     * Generates a face-down card UI component.
     */
    private JLabel createCardBackComponent() {
        JLabel label = new JLabel("<html><center>♣<br>♦ ♠ ♥<br>♣</center></html>", JLabel.CENTER);
        label.setPreferredSize(new Dimension(65, 95));
        label.setOpaque(true);
        label.setBackground(new Color(21, 101, 192));
        label.setForeground(new Color(144, 202, 249));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }
    
    /**
     * Updates the GUI board component contents based on the current GameState.
     */
    public void updateBoard() {
        Player player = gameState.getPlayer();
        ComputerPlayer computer = gameState.getComputer();
        
        // 1. Stage & Pot Info
        stageLabel.setText("STAGE: " + gameState.getCurrentStage().getDisplayName().toUpperCase());
        potLabel.setText("POT: $" + gameState.getPot());
        
        // 2. Computer Stats
        computerChipsLabel.setText("Chips: $" + computer.getChips());
        computerBetLabel.setText("Current Bet: $" + computer.getCurrentBet());
        
        // 3. Computer Cards
        computerCardsPanel.removeAll();
        if (gameState.getCurrentStage() == GameState.Stage.SHOWDOWN && !computer.isFolded()) {
            // Show cards face up at Showdown
            for (Card card : computer.getHand()) {
                computerCardsPanel.add(createCardComponent(card));
            }
        } else {
            // Otherwise show them face down (or empty if folded)
            if (computer.isFolded()) {
                computerCardsPanel.add(new JLabel("FOLDED"));
            } else {
                for (int i = 0; i < computer.getHand().size(); i++) {
                    computerCardsPanel.add(createCardBackComponent());
                }
            }
        }
        computerCardsPanel.revalidate();
        computerCardsPanel.repaint();
        
        // 4. Player Stats
        playerChipsLabel.setText("Chips: $" + player.getChips());
        playerBetLabel.setText("Current Bet: $" + player.getCurrentBet());
        
        // 5. Player Cards
        playerCardsPanel.removeAll();
        if (player.isFolded()) {
            playerCardsPanel.add(new JLabel("FOLDED"));
        } else {
            for (Card card : player.getHand()) {
                playerCardsPanel.add(createCardComponent(card));
            }
        }
        playerCardsPanel.revalidate();
        playerCardsPanel.repaint();
        
        // 6. Community Cards
        communityCardsPanel.removeAll();
        List<Card> commCards = gameState.getCommunityCards();
        for (Card card : commCards) {
            communityCardsPanel.add(createCardComponent(card));
        }
        // Fill remaining slots up to 5 cards with empty dashed placeholders
        for (int i = commCards.size(); i < 5; i++) {
            JLabel emptyCardSlot = new JLabel("", JLabel.CENTER);
            emptyCardSlot.setPreferredSize(new Dimension(65, 95));
            emptyCardSlot.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 3, 3));
            communityCardsPanel.add(emptyCardSlot);
        }
        communityCardsPanel.revalidate();
        communityCardsPanel.repaint();
        
        // 7. Button States
        boolean isGameOver = gameState.isRoundOver();
        if (isGameOver) {
            foldButton.setEnabled(false);
            checkButton.setEnabled(false);
            callButton.setEnabled(false);
            raiseButton.setEnabled(false);
            newRoundButton.setEnabled(true);
        } else {
            int toCall = gameState.getHighestBet() - player.getCurrentBet();
            
            foldButton.setEnabled(true);
            checkButton.setEnabled(toCall == 0);
            
            if (toCall > 0) {
                callButton.setText("Call $" + toCall);
                callButton.setEnabled(player.getChips() >= toCall);
                raiseButton.setText("Raise +$20");
                raiseButton.setEnabled(player.getChips() >= (toCall + 20));
            } else {
                callButton.setText("Call");
                callButton.setEnabled(false); // nothing to call
                raiseButton.setText("Bet $20");
                raiseButton.setEnabled(player.getChips() >= 20);
            }
            
            newRoundButton.setEnabled(false);
        }
    }
    
    /**
     * Wire buttons to game turn rotation actions.
     */
    private void setupActionListeners() {
        foldButton.addActionListener(e -> handlePlayerAction("FOLD"));
        checkButton.addActionListener(e -> handlePlayerAction("CHECK"));
        callButton.addActionListener(e -> handlePlayerAction("CALL"));
        raiseButton.addActionListener(e -> handlePlayerAction("RAISE"));
        
        newRoundButton.addActionListener(e -> {
            gameState.startNewRound();
            updateBoard();
        });
    }
    
    /**
     * Executes the betting turn actions and responds dynamically.
     */
    private void handlePlayerAction(String action) {
        Player player = gameState.getPlayer();
        ComputerPlayer computer = gameState.getComputer();
        
        if (action.equals("FOLD")) {
            player.fold();
            // Award pot to computer
            JOptionPane.showMessageDialog(this, "You Folded! Computer wins the pot of $" + gameState.getPot() + ".");
            computer.addChips(gameState.getPot());
            gameState.setPot(0);
            gameState.setCurrentStage(GameState.Stage.SHOWDOWN);
            updateBoard();
            return;
        }
        
        if (action.equals("CHECK")) {
            System.out.println("Player checks.");
            triggerComputerAction();
        } else if (action.equals("CALL")) {
            int toCall = gameState.getHighestBet() - player.getCurrentBet();
            player.bet(toCall);
            gameState.addToPot(toCall);
            System.out.println("Player calls $" + toCall + ". Total bet: " + player.getCurrentBet());
            
            // Player Call equalizes the bets (either matching blind or raise)
            // Once bets are equalized, we transition to the next stage.
            progressToNextStage();
        } else if (action.equals("RAISE")) {
            int toCall = gameState.getHighestBet() - player.getCurrentBet();
            // Prompt the user for a custom raise amount (minimum $20)
            String input = JOptionPane.showInputDialog(this, "Enter raise amount (minimum $20):", "Raise", JOptionPane.PLAIN_MESSAGE);
            int raiseAmount = 20; // default if parsing fails or below minimum
            try {
                if (input != null) {
                    int parsed = Integer.parseInt(input.trim());
                    if (parsed >= 20) {
                        raiseAmount = parsed;
                    }
                }
            } catch (NumberFormatException e) {
                // keep default raiseAmount = 20
            }
            int totalBet = toCall + raiseAmount;
            // Ensure the player has enough chips for the total bet
            if (totalBet > player.getChips()) {
                totalBet = player.getChips(); // go all‑in if not enough chips
                raiseAmount = totalBet - toCall;
            }
            player.bet(totalBet);
            gameState.addToPot(totalBet);
            gameState.setHighestBet(player.getCurrentBet());
            System.out.println("Player raises by $" + raiseAmount + " (total bet: " + player.getCurrentBet() + ")");
            
            // After a raise, the computer must respond
            triggerComputerAction();
        }
    }
    
    /**
     * Automation step for the computer player's turn.
     */
    private void triggerComputerAction() {
        Player player = gameState.getPlayer();
        ComputerPlayer computer = gameState.getComputer();
        int highestBetBefore = gameState.getHighestBet();
        
        int previousBotBet = computer.getCurrentBet();
        String botAction = computer.makeDecision(highestBetBefore);
        int currentBotBet = computer.getCurrentBet();
        
        int addedBet = currentBotBet - previousBotBet;
        gameState.addToPot(addedBet);
        
        System.out.println("Computer action: " + botAction + " (Bet added: $" + addedBet + ", Total bot bet: " + currentBotBet + ")");
        
        if (computer.isFolded()) {
            JOptionPane.showMessageDialog(this, "Computer Folded! You win the pot of $" + gameState.getPot() + ".");
            player.addChips(gameState.getPot());
            gameState.setPot(0);
            gameState.setCurrentStage(GameState.Stage.SHOWDOWN);
            updateBoard();
            return;
        }
        
        JOptionPane.showMessageDialog(this, "Computer action: " + botAction);
        
        // Check if bets are equalized
        if (computer.getCurrentBet() == player.getCurrentBet()) {
            progressToNextStage();
        } else {
            // Bot raised, so update highest bet. It is now the player's turn to respond to the raise.
            gameState.setHighestBet(computer.getCurrentBet());
            updateBoard();
        }
    }
    
    /**
     * Progresses the game state to the next stage and handles showdown placeholders.
     */
    private void progressToNextStage() {
        gameState.nextStage();
        
        if (gameState.getCurrentStage() == GameState.Stage.SHOWDOWN) {
            JOptionPane.showMessageDialog(this, "Showdown! Revealing computer cards.");
            
            Player player = gameState.getPlayer();
            ComputerPlayer computer = gameState.getComputer();
            
            // Determine best hand for each player
            HandEvaluator.HandScore playerScore = HandEvaluator.evaluateBestHand(player.getHand(), gameState.getCommunityCards());
            HandEvaluator.HandScore computerScore = HandEvaluator.evaluateBestHand(computer.getHand(), gameState.getCommunityCards());
            
            int comparison = playerScore.compareTo(computerScore);
            
            String winnerMsg;
            if (comparison > 0) {
                winnerMsg = player.getName() + " wins with " + playerScore.getType().getName() + "!";
                player.addChips(gameState.getPot());
            } else if (comparison < 0) {
                winnerMsg = computer.getName() + " wins with " + computerScore.getType().getName() + "!";
                computer.addChips(gameState.getPot());
            } else {
                winnerMsg = "Split pot! Both players have " + playerScore.getType().getName() + ".";
                int splitPot = gameState.getPot() / 2;
                player.addChips(splitPot);
                computer.addChips(splitPot);
                // Award odd chip to player
                int oddChip = gameState.getPot() % 2;
                player.addChips(oddChip);
            }
            
            JOptionPane.showMessageDialog(this, winnerMsg, "Round Over", JOptionPane.INFORMATION_MESSAGE);
            gameState.setPot(0);
        }
        
        updateBoard();
    }
}
