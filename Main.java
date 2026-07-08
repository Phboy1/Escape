package Escape;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

public class Main extends Canvas implements KeyListener {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static final int FRAME_DELAY = 16;
    static final int SIZE = 50;
    static final int PLAYER_SPEED = 5;
    static final int TILE_SIZE = 32;

    static final int NAVBAR_Y = 100;

    // Tiles

    static final int WALL_BLOCK = 0;
    static final int PLAYER_BLOCK = 1;
    static final int ENEMY_BLOCK = 2;
    static final int JAIL_BLOCK = 3;

    static int playerX = WIDTH / 2;
    static int playerY = HEIGHT / 2;

    static boolean leftHeld  = false;
    static boolean rightHeld = false;
    static boolean upHeld    = false;
    static boolean downHeld  = false;

    static BufferedImage[] backgroundImg = new BufferedImage[1];

    static BufferedImage[] tiles = new BufferedImage[4];

    static ArrayList<ArrayList<Character>> level = new ArrayList<ArrayList<Character>>();


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

        //Images

        try {
            backgroundImg[0] = ImageIO.read(new File("Escape/images/bg.png"));

            
            tiles[WALL_BLOCK] = ImageIO.read(new File("Escape/images/wallblock.png"));
            tiles[PLAYER_BLOCK] = ImageIO.read(new File("Escape/images/playerblock.png"));
            tiles[ENEMY_BLOCK] = ImageIO.read(new File("Escape/images/enemyblock.png"));
            tiles[JAIL_BLOCK] = ImageIO.read(new File("Escape/images/jailblock.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Level loading

        try (BufferedReader br = new BufferedReader(new FileReader("Escape/level.txt"))) {
            String line = br.readLine();

            while (line != null)
            {
                ArrayList<Character> newRow = new ArrayList<>();
                for (int i = 0; i < line.length(); i++)
                {
                    newRow.add(line.charAt(i));
                    System.out.println(line.charAt(i));
                }

                level.add(newRow);
                line = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        g2d.drawImage(backgroundImg[0], 0, 0, null);
        int i = 0;
        int j = 0;

        int xOffset = (WIDTH - level.get(0).size() * TILE_SIZE)/2;
        int yOffset = (HEIGHT - level.size() * TILE_SIZE + NAVBAR_Y)/2;

        for (ArrayList<Character> line : level)
        {
            for (Character character : line)
            {
                if (character == 'x')
                {
                    g2d.drawImage(tiles[WALL_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                }
                else if (character == 'p')
                {
                    g2d.drawImage(tiles[PLAYER_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                }
                else if (character == '1' || character == '2')
                {
                    g2d.drawImage(tiles[ENEMY_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                }
                else if (character == 'e')
                {
                    g2d.drawImage(tiles[JAIL_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                }
                j++;
            }
            j = 0;
            i++;
        }
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