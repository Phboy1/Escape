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

    static final int MAX_ENEMIES = 10;

    static final long ENEMY_SPEED = 250000000L;

    static final int NAVBAR_Y = 100;

    static int playerRow = 0;
    static int playerCol = 0;

    static ArrayList<Integer> enemyX = new ArrayList<Integer>();
    static ArrayList<Integer> enemyY = new ArrayList<Integer>();
    static ArrayList<String> enemyDir = new ArrayList<String>();

    // Tiles

    static final int WALL_BLOCK = 0;
    static final int PLAYER_BLOCK = 1;
    static final int ENEMY_BLOCK = 2;
    static final int JAIL_BLOCK = 3;


    static boolean leftPressed = false;
    static boolean rightPressed = false;
    static boolean upPressed = false;
    static boolean downPressed = false;

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
            int j = 0;

            while (line != null)
            {
                ArrayList<Character> newRow = new ArrayList<>();
                for (int i = 0; i < line.length(); i++)
                {
                    newRow.add(line.charAt(i));
                    
                    if (line.charAt(i) == '1')
                    {
                        enemyX.add(i);
                        enemyY.add(j);
                        enemyDir.add(((int) (Math.random() * 2) + 1 % 2 == 0 ? "left" : "right"));
                    }
                    else if (line.charAt(i) == '2')
                    {
                        enemyX.add(i);
                        enemyY.add(j);
                        enemyDir.add(((int) (Math.random() * 2) + 1 % 2 == 0 ? "left" : "right"));
                    }
                }

                level.add(newRow);
                line = br.readLine();
                j++;
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
        if (upPressed)
        {
            movePlayer(0, -1);
            upHeld = true;
            upPressed = false;
        }
        if (rightPressed)
        {
            movePlayer(1, 0);
            rightHeld = true;
            rightPressed = false;
        }
        if (leftPressed)
        {
            movePlayer(-1, 0);
            leftHeld = true;
            leftPressed = false;
        }
        if (downPressed)
        {
            movePlayer(0, 1);
            downHeld = true;
            downPressed = false;
        }
        System.out.printf("Player row: %d%n", playerRow);
        System.out.printf("Player col: %d%n", playerCol);

        for (int i = 0; i < enemyX.size(); i++)
        {
            if (enemyDir.get(i).equals("left"))
            {
                if (level.get(enemyX.get(i) - 1).get(enemyY.get(i)).equals('x') || level.get(enemyX.get(i) - 1).get(enemyY.get(i)).equals('1') || level.get(enemyX.get(i) - 1).get(enemyY.get(i)).equals('2'))
                {
                    enemyDir.set(i, "right");
                }
                else
                {
                    level.get(enemyX.get(i)).set(enemyY.get(i), '_');
                    level.get(enemyX.get(i) - 1).set(enemyY.get(i), '1');
                }
            }
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
                    playerCol = j;
                    playerRow = i;
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

    public static void movePlayer(int x, int y)
    {
        if (level.get(playerRow + y).get(playerCol + x) == 'x')
        {
            return;
        }
        else
        {
            level.get(playerRow).set(playerCol, '_');
            playerRow += y;
            playerCol += x;

            level.get(playerRow).set(playerCol, 'p');
            
        }
    }

    // --- INPUT METHODS ---

    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && !leftHeld) {
            leftPressed = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !rightHeld) {
            rightPressed = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && !upHeld) {
            upPressed = true;
        }
        if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && !downHeld) {
            downPressed = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            leftHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            rightHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            upHeld = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            downHeld = false;
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}