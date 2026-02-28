package rps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    private JTextArea resultsArea;
    private JTextField playerWinsField, computerWinsField, tiesField;
    private int playerWins = 0, computerWins = 0, ties = 0;
    private int rockCount = 0, paperCount = 0, scissorsCount = 0;
    private String lastPlayerMove = "";

    // Strategies
    private Strategy randomStrategy = new RandomStrategy();
    private Strategy cheatStrategy = new Cheat();
    private Strategy leastUsedStrategy = new LeastUsed();
    private Strategy mostUsedStrategy = new MostUsed();
    private Strategy lastUsedStrategy = new LastUsed();

    private Random rand = new Random();

    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton quitButton;

    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel: Buttons ---
        rockButton = new JButton(resizeIcon("/images/rock.png", 80, 80));
        paperButton = new JButton(resizeIcon("/images/paper.png", 80, 80));
        scissorsButton = new JButton(resizeIcon("/images/scissors.png", 80, 80));
        quitButton = new JButton("Quit");

        Dimension buttonSize = new Dimension(100, 100);
        rockButton.setPreferredSize(buttonSize);
        paperButton.setPreferredSize(buttonSize);
        scissorsButton.setPreferredSize(buttonSize);
        quitButton.setPreferredSize(buttonSize);

        JPanel topPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Choose your move"));
        topPanel.add(rockButton);
        topPanel.add(paperButton);
        topPanel.add(scissorsButton);
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results ---
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Results"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Stats ---
        playerWinsField = new JTextField("0");
        playerWinsField.setEditable(false);
        computerWinsField = new JTextField("0");
        computerWinsField.setEditable(false);
        tiesField = new JTextField("0");
        tiesField.setEditable(false);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Stats"));
        bottomPanel.add(new JLabel("Player Wins:"));
        bottomPanel.add(playerWinsField);
        bottomPanel.add(new JLabel("Computer Wins:"));
        bottomPanel.add(computerWinsField);
        bottomPanel.add(new JLabel("Ties:"));
        bottomPanel.add(tiesField);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Button Action Listener ---
        ActionListener moveListener = e -> {
            String playerMove = "";
            if (e.getSource() == rockButton) playerMove = "R";
            else if (e.getSource() == paperButton) playerMove = "P";
            else if (e.getSource() == scissorsButton) playerMove = "S";
            else if (e.getSource() == quitButton) System.exit(0);

            if (!playerMove.isEmpty()) playRound(playerMove);
        };

        rockButton.addActionListener(moveListener);
        paperButton.addActionListener(moveListener);
        scissorsButton.addActionListener(moveListener);
        quitButton.addActionListener(moveListener);
    }

    // --- Game Logic ---
    private void playRound(String playerMove) {
        switch (playerMove) {
            case "R": rockCount++; break;
            case "P": paperCount++; break;
            case "S": scissorsCount++; break;
        }

        int probability = rand.nextInt(100) + 1;
        Strategy chosenStrategy;

        if (probability <= 10) chosenStrategy = cheatStrategy;
        else if (probability <= 30) chosenStrategy = leastUsedStrategy;
        else if (probability <= 50) chosenStrategy = mostUsedStrategy;
        else if (probability <= 70) chosenStrategy = lastUsedStrategy;
        else chosenStrategy = randomStrategy;

        String computerMove = chosenStrategy.getMove(playerMove);
        String result = determineWinner(playerMove, computerMove);

        resultsArea.append(result + " (Computer: " + chosenStrategy.getClass().getSimpleName() + ")\n");

        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
        lastPlayerMove = playerMove;
    }

    private String determineWinner(String player, String computer) {
        if (player.equals(computer)) {
            ties++;
            return moveName(player) + " equals " + moveName(computer) + ". It's a tie!";
        }
        if ((player.equals("R") && computer.equals("S")) ||
                (player.equals("P") && computer.equals("R")) ||
                (player.equals("S") && computer.equals("P"))) {
            playerWins++;
            return moveName(player) + " beats " + moveName(computer) + ". Player wins!";
        } else {
            computerWins++;
            return moveName(computer) + " beats " + moveName(player) + ". Computer wins!";
        }
    }

    private String moveName(String move) {
        switch (move) {
            case "R": return "Rock";
            case "P": return "Paper";
            case "S": return "Scissors";
            default: return "Unknown";
        }
    }

    private ImageIcon resizeIcon(String path, int w, int h) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // --- Inner Strategy Classes ---
    private class LeastUsed implements Strategy {
        public String getMove(String playerMove) {
            int min = Math.min(rockCount, Math.min(paperCount, scissorsCount));
            if (min == rockCount) return "P";
            else if (min == paperCount) return "S";
            else return "R";
        }
    }

    private class MostUsed implements Strategy {
        public String getMove(String playerMove) {
            int max = Math.max(rockCount, Math.max(paperCount, scissorsCount));
            if (max == rockCount) return "P";
            else if (max == paperCount) return "S";
            else return "R";
        }
    }

    private class LastUsed implements Strategy {
        public String getMove(String playerMove) {
            if (lastPlayerMove.isEmpty()) return randomStrategy.getMove(playerMove);
            switch (lastPlayerMove) {
                case "R": return "P";
                case "P": return "S";
                case "S": return "R";
                default: return randomStrategy.getMove(playerMove);
            }
        }
    }

    // --- Main ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
            frame.setVisible(true);
        });
    }
}