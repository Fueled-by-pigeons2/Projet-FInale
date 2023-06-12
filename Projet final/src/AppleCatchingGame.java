import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class AppleCatchingGame extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 500; // Largeur de la fenêtre du jeu
    private static final int HEIGHT = 500; // Hauteur de la fenêtre du jeu
    private static final int PLAYER_WIDTH = 50; // Largeur du joueur
    private static final int PLAYER_HEIGHT = 20; // Hauteur du joueur
    private static final int APPLE_WIDTH = 30; // Largeur de la pomme
    private static final int APPLE_HEIGHT = 30; // Hauteur de la pomme
    private static final int APPLE_SPEED = 3; // Vitesse de déplacement des pommes
    private static final int PLAYER_SPEED = 5; // Vitesse de déplacement du joueur

    private int playerX; // Position en X du joueur
    private List<Integer> appleXList; // Liste des positions en X des pommes
    private List<Integer> appleYList; // Liste des positions en Y des pommes
    private int score; // Score du joueur

    private Timer timer; // Timer pour mettre à jour le jeu
    private boolean gameRunning; // Indicateur de l'état du jeu (en cours ou terminé)
    private JButton restartButton; // Bouton pour redémarrer le jeu
    private JButton quitButton; // Bouton pour quitter le jeu

    private boolean leftKeyPressed; // Indicateur de la touche de gauche enfoncée
    private boolean rightKeyPressed; // Indicateur de la touche de droite enfoncée

  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppleCatchingGame game = new AppleCatchingGame();
            game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            JFrame frame = new JFrame("Apple Catching Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            
            JButton startButton = new JButton("Start");
            startButton.addActionListener(game); // Ajoute un écouteur d'événement au bouton pour commencer
            
            JButton restartButton = new JButton("Restart");
            restartButton.addActionListener(game); // Ajoute un écouteur d'événement au bouton pour reccommencer
            restartButton.setEnabled(true); // Active le bouton "Restart"
            
            JButton quitButton = new JButton("Quit");
            quitButton.addActionListener(game); // Ajoute un écouteur d'événement au bouton pouor quitter
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(startButton);
            buttonPanel.add(restartButton);
            buttonPanel.add(quitButton);
            
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
            frame.getContentPane().add(game, BorderLayout.CENTER);
            
            frame.pack();
            frame.requestFocusInWindow(); // Demande le focus clavier initial
            frame.setVisible(true); // Rend la fenêtre visible
        });
    }
//    JFrame est créée avec le titre "Apple Catching Game". Les boutons "Start", "Restart" et "Quit" sont créés 
//    et des écouteurs d'événement sont ajoutés à ces boutons pour réagir aux clics. Un panneau JPanel est créé pour 
//    contenir les boutons.
//
//    La mise en page de la fenêtre est configurée avec BorderLayout. Le panneau de boutons est 
//    ajouté dans la partie nord de la fenêtre et l'objet AppleCatchingGame est ajouté dans la partie centrale.
    public AppleCatchingGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2; // Position initiale du joueur sur l'axe X
        appleXList = new ArrayList<>(); // Liste des positions X des pommes
        appleYList = new ArrayList<>(); // Liste des positions Y des pommes
        score = 0; // Score initial
        
        restartButton = new JButton("Restart"); // Crée un bouton pour receommencer
        restartButton.addActionListener(this); // Ajoute un écouteur d'événement au bouton pour recommencer
        restartButton.setEnabled(true); // Active le bouton "Restart"
        
        quitButton = new JButton("Quit"); // Crée un bouton pour quitter
        quitButton.addActionListener(this); // Ajoute un écouteur d'événement au bouton pour quitter
        
        JPanel buttonPanel = new JPanel(); // Crée un panneau pour les boutons
        buttonPanel.add(restartButton); // Ajoute le bouton "Restart" au panneau
        buttonPanel.add(quitButton); // Ajoute le bouton "Quit" au panneau
        
        timer = new Timer(10, this); // Crée un minuteur avec un délai de 10 ms et ajoute un écouteur d'événement
        gameRunning = false; // Le jeu n'est pas en cours
        
        // Ajoute un écouteur d'événement pour les touches du clavier
        addKeyListener(this);
        
        // Demande le focus sur le panneau pour que les événements clavier soient détectés
        setFocusable(true);
        requestFocusInWindow();
        
        // Permet la détection des touches de déplacement (flèches) en tant que touches de traversée
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
    }

    private void movePlayer() {
        if (leftKeyPressed && !rightKeyPressed && playerX > 0) {
            playerX -= PLAYER_SPEED; // Déplace le joueur vers la gauche
        }
        if (rightKeyPressed && !leftKeyPressed && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED; // Déplace le joueur vers la droite
        }
    }

    private void moveApples() {
        for (int i = 0; i < appleYList.size(); i++) {
            int appleY = appleYList.get(i);
            appleY += APPLE_SPEED; // Déplace la pomme vers le bas
            appleYList.set(i, appleY);
            if (appleY >= HEIGHT) {
                // Supprime la pomme si elle atteint le bas de l'écran
                appleXList.remove(i);
                appleYList.remove(i);
                i--;
            }
        }
        if (appleYList.size() < 1) {
            // Génère une nouvelle pomme si aucune pomme n'est présente sur l'écran
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
                // Vérifie si le joueur entre en collision avec une pomme
                score++; // Incrémente le score
                appleXList.remove(i); // Supprime la pomme
                appleYList.remove(i);
                i--;
            }
            if (appleY >= 480) {
                // Arrête le jeu si une pomme atteint le bas de l'écran
                stopGame();
            }
        }
    }

    private void generateApple() {
        Random random = new Random();
        int appleX = random.nextInt(WIDTH - APPLE_WIDTH); // Génère une position X aléatoire pour la pomme
        int appleY = 0; // Position initiale de la pomme en haut de l'écran
        appleXList.add(appleX); // Ajoute la position X de la pomme à la liste
        appleYList.add(appleY); // Ajoute la position Y de la pomme à la liste
    }

    private void startGame() {
        gameRunning = true; // Démarre le jeu
        leftKeyPressed = false; // Réinitialise l'état de la touche de gauche
        rightKeyPressed = false; // Réinitialise l'état de la touche de droite
        restartButton.setEnabled(true); // Active le bouton de redémarrage
        quitButton.setEnabled(true); // Active le bouton de quitter
        addKeyListener(this); // Ajoute un écouteur d'événements de clavier
        requestFocusInWindow(); // Demande le focus sur le panneau
        timer.start(); // Démarre le timer du jeu
    }

    private void restartGame() {
        playerX = WIDTH / 2 - PLAYER_WIDTH / 2; // Réinitialise la position du joueur au centre de l'écran
        appleXList.clear(); // Supprime toutes les positions X des pommes
        appleYList.clear(); // Supprime toutes les positions Y des pommes
        score = 0; // Réinitialise le score à 0
        gameRunning = true; // Démarre le jeu
        restartButton.setEnabled(true); // Active le bouton de redémarrage
        quitButton.setEnabled(true); // Active le bouton de quitter
        addKeyListener(this); // Ajoute un écouteur d'événements de clavier
        requestFocusInWindow(); // Demande le focus sur le panneau
        timer.start(); // Démarre le timer du jeu
    }

    private void quitGame() {
        System.exit(0); // Quitte le jeu en fermant l'application
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Exécuter les actions du jeu uniquement lorsque le jeu est en cours
        if (gameRunning) {
            movePlayer(); // Déplacer le joueur
            moveApples(); // Déplacer les pommes
            checkCollision(); // Vérifier les collisions
            repaint(); // Redessiner le composant
        }

        // Vérifier la source de l'événement
        if (e.getSource() instanceof JButton) {
            JButton source = (JButton) e.getSource();
            if (source.getText().equals("Start")) {
                startGame(); // Démarrer le jeu
            } else if (source.getText().equals("Restart")) {
                restartGame(); // Redémarrer le jeu
            } else if (source.getText().equals("Quit")) {
                quitGame(); // Quitter le jeu
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.GREEN);
        g.fillRect(playerX, HEIGHT - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT); // Dessiner le joueur

        g.setColor(Color.RED);
        for (int i = 0; i < appleXList.size(); i++) {
            int appleX = appleXList.get(i);
            int appleY = appleYList.get(i);
            g.fillOval(appleX, appleY, APPLE_WIDTH, APPLE_HEIGHT); // Dessiner les pommes
        }

        if (gameRunning) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20); // Afficher le score
        }
        
        if (!gameRunning) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.ITALIC, 25));
            g.drawString("Appuyez sur Démarrer pour commencer", 10, 35); // Afficher un message pour démarrer
            
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.ITALIC, 20));
            g.drawString("Utilisez les touches fléchées gauche et droite pour vous déplacer", 10, 65); // Afficher un message d'instructions
        }
    }
        
        
        
        
    
    
    private void stopGame() {
        gameRunning = false; // Arrêter le jeu
        timer.stop(); // Arrêter le timer

        // Afficher une boîte de dialogue avec le message de fin de jeu et le score
        JOptionPane.showMessageDialog(this, "Partie terminée ! Votre score : " + score + '\n' + "Appuyez sur redémarrer pour rejouer", "Partie terminée", JOptionPane.INFORMATION_MESSAGE);

        restartButton.setEnabled(true); // Activer le bouton de redémarrage
        quitButton.setEnabled(true); // Activer le bouton de quitter
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = true; // La touche de gauche est enfoncée
        } else if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = true; // La touche de droite est enfoncée
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            leftKeyPressed = false; // La touche de gauche est relâchée
        } else if (key == KeyEvent.VK_RIGHT) {
            rightKeyPressed = false; // La touche de droite est relâchée
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Cette méthode ne fait rien, car nous n'avons pas besoin de traiter les événements de frappe de touches
    }
    
}
