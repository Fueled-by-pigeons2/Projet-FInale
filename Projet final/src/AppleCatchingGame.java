import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AppleCatchingGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 20;
    private static final int APPLE_WIDTH = 30;
    private static final int APPLE_HEIGHT = 30;
    private static final int APPLE_SPEED = 3;
    private static final int PLAYER_SPEED = 5;
    
    int asdasdx=0;
    
    private int playerX;
    private List<Integer> appleXList;
    private List<Integer> appleYList;
    private int score;

    private Timer timer;
    private boolean gameRunning;
    private JButton restartButton;
    private JButton quitButton;

    private boolean leftKeyPressed;
    private boolean rightKeyPressed;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppleCatchingGame game = new AppleCatchingGame();
            game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            JFrame frame = new JFrame("Apple Catching Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            JButton startButton = new JButton("Start");
            startButton.addActionListener(game);

            JButton restartButton = new JButton("Restart");
            restartButton.addActionListener(game);
            restartButton.setEnabled(true);

            JButton quitButton = new JButton("Quit");
            quitButton.addActionListener(game);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(startButton);
            buttonPanel.add(restartButton);
            buttonPanel.add(quitButton);

            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
            frame.getContentPane().add(game, BorderLayout.CENTER);

            frame.pack();
            frame.requestFocusInWindow(); // Request initial keyboard focus
            frame.setVisible(true);
        });
    }

    public AppleCatchingGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        appleXList = new ArrayList<>();
        appleYList = new ArrayList<>();
        score = 0;

        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        restartButton.setEnabled(true);

        quitButton = new JButton("Quit");
        quitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);
        buttonPanel.add(quitButton);

        timer = new Timer(10, this);
        gameRunning = false;

        // Add key listener
        addKeyListener(this);

        // Request focus on the panel
        setFocusable(true);
        requestFocusInWindow();
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null); // Allow arrow keys to be detected
    }

    private void movePlayer() {
        if (leftKeyPressed && !rightKeyPressed && playerX > 0) {
            playerX -= PLAYER_SPEED;
        }

        if (rightKeyPressed && !leftKeyPressed && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }
    }

    private void moveApples() {
        for (int i = 0; i < appleYList.size(); i++) {
            int appleY = appleYList.get(i);
            appleY += APPLE_SPEED;
            appleYList.set(i, appleY);

            if (appleY >= HEIGHT) {
                appleXList.remove(i);
                appleYList.remove(i);
                i--;
            }
        }

        if (appleYList.size() < 1) {
            generateApple();
        }
    }

    
    
    private void checkCollision() {
        Rectangle playerRect = new Rectangle(playerX, HEIGHT - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);

        for (int i = 0; i < appleXList.size(); i++) {
            int appleX = appleXList.get(i);
            int appleY = appleYList.get(i);
            Rectangle appleRect = new Rectangle(appleX, appleY, APPLE_WIDTH, APPLE_HEIGHT);

            if (playerRect.intersects(appleRect)) {
                score++;
                appleXList.remove(i);
                appleYList.remove(i);
                i--;
            }
            if (appleY >= 480) {
                stopGame();
            }
        }

        
    }

    private void generateApple() {
        Random random = new Random();
        int appleX = random.nextInt(WIDTH - APPLE_WIDTH);
        int appleY = 0;
        appleXList.add(appleX);
        appleYList.add(appleY);
    }

    private void startGame() {
        gameRunning = true;
        leftKeyPressed = false;
        rightKeyPressed = false;
        restartButton.setEnabled(true); // Enable the restart button
        quitButton.setEnabled(true);
        addKeyListener(this);
        requestFocusInWindow();
        timer.start();
        
    }

    private void restartGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        appleXList.clear();
        appleYList.clear();
        score = 0;
        gameRunning = true;
        restartButton.setEnabled(true);
        quitButton.setEnabled(true);
        addKeyListener(this);
        requestFocusInWindow();
        timer.start();
    }

    private void quitGame() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            movePlayer();
            moveApples();
            checkCollision();
            repaint();
        }

        if (e.getSource() instanceof JButton) {
            JButton source = (JButton) e.getSource();
            if (source.getText().equals("Start")) {
                startGame();
            } else if (source.getText().equals("Restart")) {
                restartGame();
            } else if (source.getText().equals("Quit")) {
                quitGame();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(playerX, HEIGHT - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);

        g.setColor(Color.RED);
        for (int i = 0; i < appleXList.size(); i++) {
            int appleX = appleXList.get(i);
            int appleY = appleYList.get(i);
            g.fillOval(appleX, appleY, APPLE_WIDTH, APPLE_HEIGHT);
        }
        if (gameRunning) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        }
        
        if(!gameRunning) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.ITALIC, 25));
        g.drawString("Press start to begin ", 10, 35);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.ITALIC, 20));
        g.drawString("Use left and right arrow keys to move", 10, 65);
        }
        
        
        
        
    }
    
    private void stopGame() {
        gameRunning = false;
        timer.stop();

        // Display a message dialog
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score + '\n' + "Press restart to play again" , "Game Over", JOptionPane.INFORMATION_MESSAGE);

        restartButton.setEnabled(true);
        quitButton.setEnabled(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = true;
        } else if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = false;
        } else if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    
}
