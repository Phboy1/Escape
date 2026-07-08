import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends Canvas implements KeyListener {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;
    static final int SIZE = 50;
    static final int PLAYER_SPEED = 5;

    static int playerX = WIDTH / 2;
    static int playerY = HEIGHT / 2;

    static boolean leftHeld  = false;
    static boolean rightHeld = false;
    static boolean upHeld    = false;
    static boolean downHeld  = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clean Game Loop");
        Main game = new Main();
        game.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        game.addKeyListener(game);
        game.requestFocus();

        game.createBufferStrategy(3);
        BufferStrategy bs = game.getBufferStrategy();

        while (true) {
            // 1. Logic (Thinking)
            update();

            // 2. Rendering (Showing)
            Graphics g = bs.getDrawGraphics();
            Graphics2D g2d = (Graphics2D) g;

            // Turn on Anti-Aliasing for smooth edges
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            draw(g2d);

            g.dispose();
            bs.show();

            // 3. Timing
            try {
                Thread.sleep(FRAME_DELAY);
            }
            catch (Exception e) {
            }
        }
    }

    // --- GAME ENGINE METHODS ---

    public static void update() {
        if (leftHeld) {
            playerX -= PLAYER_SPEED;
        }
        if (rightHeld) {
            playerX += PLAYER_SPEED;
        }
        if (upHeld) {
            playerY -= PLAYER_SPEED;
        }
        if (downHeld) {
            playerY += PLAYER_SPEED;
        }
    }

    public static void draw(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        g2d.setColor(Color.CYAN);
        g2d.fillRect(playerX, playerY, SIZE, SIZE);
    }

    // --- INPUT METHODS ---

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftHeld = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightHeld = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upHeld = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downHeld = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downHeld = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}