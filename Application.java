import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 25;
    private static final int BOARD_WIDTH = 20;
    private static final int BOARD_HEIGHT = 20;
    private static final int DELAY = 100;
    
    private ArrayList<Point> snake;
    private Point food;
    private int direction; // 0=UP, 1=RIGHT, 2=DOWN, 3=LEFT
    private boolean running;
    private Timer timer;
    private Random random;
    private int score;
    
    public SnakeGame() {
        random = new Random();
        setPreferredSize(new Dimension(BOARD_WIDTH * TILE_SIZE, BOARD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        
        initGame();
    }
    
    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2));
        snake.add(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2 + 1));
        snake.add(new Point(BOARD_WIDTH / 2, BOARD_HEIGHT / 2 + 2));
        
        direction = 0; // Start moving up
        score = 0;
        spawnFood();
        running = true;
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH);
            y = random.nextInt(BOARD_HEIGHT);
        } while (isSnakeAt(x, y));
        
        food = new Point(x, y);
    }
    
    private boolean isSnakeAt(int x, int y) {
        for (Point p : snake) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }
    
    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x, head.y);
        
        switch (direction) {
            case 0: newHead.y--; break; // UP
            case 1: newHead.x++; break; // RIGHT
            case 2: newHead.y++; break; // DOWN
            case 3: newHead.x--; break; // LEFT
        }
        
        // Check collision with walls
        if (newHead.x < 0 || newHead.x >= BOARD_WIDTH || 
            newHead.y < 0 || newHead.y >= BOARD_HEIGHT) {
            gameOver();
            return;
        }
        
        // Check collision with itself
        for (Point p : snake) {
            if (p.equals(newHead)) {
                gameOver();
                return;
            }
        }
        
        snake.add(0, newHead);
        
        // Check if food eaten
        if (newHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }
    
    private void gameOver() {
        running = false;
        timer.stop();
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Game Over! Score: " + score + "\nPlay again?",
            "Game Over",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            initGame();
        } else {
            System.exit(0);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 45));
                }
                g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Prevent 180-degree turns
        if (key == KeyEvent.VK_UP && direction != 2) {
            direction = 0;
        } else if (key == KeyEvent.VK_RIGHT && direction != 3) {
            direction = 1;
        } else if (key == KeyEvent.VK_DOWN && direction != 0) {
            direction = 2;
        } else if (key == KeyEvent.VK_LEFT && direction != 1) {
            direction = 3;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            SnakeGame game = new SnakeGame();
            
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
