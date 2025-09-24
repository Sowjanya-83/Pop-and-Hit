import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class WhacAMole {
    int boardWidth = 550;
    int boardHeight = 630;

    JFrame frame = new JFrame("Mario: Pop and Hit");
    JLabel scoreLabel = new JLabel();
    JLabel bestScoreLabel = new JLabel();
    JLabel timerLabel = new JLabel();       
    JLabel gameOverLabel = new JLabel();

    JPanel textPanel = new JPanel();
    JPanel boardContainer = new JPanel(null);
    JPanel boardPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;

    JButton currMoleTile;
    JButton currPlantTile;

    JButton restartButton = new JButton("Restart Game");

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    Timer gameOverAnimationTimer;
    Timer countdownTimer;    

    int score = 0;
    int bestScore = 0;
    int animationStep = 0;
    int timeLeft = 30; 

    WhacAMole() {
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel headingLabel = new JLabel("Pop And Hit");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headingLabel.setHorizontalAlignment(JLabel.CENTER);
        headingLabel.setOpaque(true);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(headingLabel, BorderLayout.CENTER);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: " + score);

        bestScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        bestScoreLabel.setHorizontalAlignment(JLabel.CENTER);
        bestScoreLabel.setText("Best: " + bestScore);

        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setText("Time: " + timeLeft);

        textPanel.setLayout(new GridLayout(1, 3));
        textPanel.add(scoreLabel);
        textPanel.add(timerLabel);
        textPanel.add(bestScoreLabel);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(titlePanel);
        topPanel.add(textPanel);
        frame.add(topPanel, BorderLayout.NORTH);

        boardContainer.setPreferredSize(new Dimension(boardWidth, boardHeight - 150));
        boardContainer.setLayout(null);
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBounds(0, 0, boardWidth, boardHeight - 150);
        boardPanel.setOpaque(false);
        boardContainer.add(boardPanel);

        // Load icons
        Image plantImg = new ImageIcon(getClass().getResource("./piranha.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));
        Image moleImg = new ImageIcon(getClass().getResource("./monty.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);
            tile.addActionListener(e -> {
                JButton clickedTile = (JButton) e.getSource();
                if (clickedTile == currMoleTile) {
                    score += 10;
                    scoreLabel.setText("Score: " + score);
                    if (score > bestScore) {
                        bestScore = score;
                        bestScoreLabel.setText("Best: " + bestScore);
                    }
                } else if (clickedTile == currPlantTile) {
                    endGame("Game Over! You clicked the Cactus!\nYour Score: " + score);
                }
            });
        }

        gameOverLabel.setText("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 10));
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);
        gameOverLabel.setForeground(new Color(255, 0, 0, 0));
        gameOverLabel.setBounds(0, 200, boardWidth, 100);
        gameOverLabel.setVisible(false);
        boardContainer.add(gameOverLabel);

        frame.add(boardContainer, BorderLayout.CENTER);

        // Timers
        setMoleTimer = new Timer(700, e -> {
            if (currMoleTile != null) currMoleTile.setIcon(null);
            int num = random.nextInt(9);
            if (board[num] != currPlantTile) {
                currMoleTile = board[num];
                currMoleTile.setIcon(moleIcon);
            }
        });

        setPlantTimer = new Timer(500, e -> {
            if (currPlantTile != null) currPlantTile.setIcon(null);
            int num = random.nextInt(9);
            if (board[num] != currMoleTile) {
                currPlantTile = board[num];
                currPlantTile.setIcon(plantIcon);
            }
        });

        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                endGame("Time's Up!\nYour Score: " + score);
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();
        countdownTimer.start();

        bottomPanel.setLayout(new FlowLayout());
        restartButton.setFont(new Font("Arial", Font.BOLD, 20));
        bottomPanel.add(restartButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        restartButton.addActionListener(e -> {
            score = 0;
            timeLeft = 30;
            scoreLabel.setText("Score: " + score);
            bestScoreLabel.setText("Best: " + bestScore);
            timerLabel.setText("Time: " + timeLeft);

            gameOverLabel.setVisible(false);
            gameOverLabel.setFont(new Font("Arial", Font.BOLD, 10));
            gameOverLabel.setForeground(new Color(255, 0, 0, 0));
            animationStep = 0;

            if (currMoleTile != null) currMoleTile.setIcon(null);
            if (currPlantTile != null) currPlantTile.setIcon(null);
            currMoleTile = null;
            currPlantTile = null;

            for (JButton b : board) b.setEnabled(true);

            setMoleTimer.restart();
            setPlantTimer.restart();
            countdownTimer.restart();
        });

        frame.setVisible(true);
    }

    private void endGame(String message) {
        scoreLabel.setText("Score: " + score);
        setMoleTimer.stop();
        setPlantTimer.stop();
        countdownTimer.stop();

        for (JButton b : board) b.setEnabled(false);

        // Show dialog box with Game Over and score
        JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        showGameOverAnimated();
    }

    private void showGameOverAnimated() {
        animationStep = 0;
        gameOverLabel.setVisible(true);
        gameOverAnimationTimer = new Timer(50, e -> {
            animationStep++;
            int fontSize = 10 + animationStep * 2;
            int alpha = Math.min(255, animationStep * 25);
            gameOverLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
            gameOverLabel.setForeground(new Color(255, 0, 0, alpha));
            gameOverLabel.repaint();
            if (animationStep >= 10) {
                gameOverAnimationTimer.stop();
            }
        });
        gameOverAnimationTimer.start();
    }

    public static void main(String[] args) {
        new WhacAMole();
    }
}
