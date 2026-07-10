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

    static final long SECONDS_TO_NANO = 1000000000L;

    static int cookies = 0;

    static final int MENU = 0;
    static final int PLAYING = 1;
    static final int WIN = 2;
    static final int LOSE = 3;

    static int state = 0;

    static final int MAX_ENEMIES = 10;

    static final long ENEMY_SPEED = 250000000L;

    static final int NAVBAR_Y = 100;

    static int playerRow = 0;
    static int playerCol = 0;

    static ArrayList<Integer> enemyX = new ArrayList<Integer>();
    static ArrayList<Integer> enemyY = new ArrayList<Integer>();
    static ArrayList<String> enemyDir = new ArrayList<String>();
    static ArrayList<Long> enemyLastMove = new ArrayList<Long>();
    static ArrayList<Character> enemyType = new ArrayList<Character>();

    // Tiles

    static final int WALL_BLOCK = 0;
    static final int PLAYER_BLOCK = 1;
    static final int ENEMY_BLOCK = 2;
    static final int JAIL_BLOCK = 3;
    static final int COOKIE = 4;
    static final int CHECKERED_BLOCK = 5;


    static boolean leftPressed = false;
    static boolean rightPressed = false;
    static boolean upPressed = false;
    static boolean downPressed = false;

    static boolean leftHeld  = false;
    static boolean rightHeld = false;
    static boolean upHeld    = false;
    static boolean downHeld  = false;

    static boolean open = false;

    static BufferedImage[] backgroundImg = new BufferedImage[1];

    static BufferedImage[] tiles = new BufferedImage[6];

    static ArrayList<ArrayList<Character>> level = new ArrayList<ArrayList<Character>>();

    static long timer = 0; 

    static int cookiesNeeded = 0;

    static long currentTime = System.nanoTime();

    static long startTime = 0;

    static long elaspedTime;


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
            tiles[COOKIE] = ImageIO.read(new File("Escape/images/cookie.png"));
            tiles[CHECKERED_BLOCK] = ImageIO.read(new File("Escape/images/checkeredblock.png"));

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
                        enemyDir.add(((int) ((Math.random() * 2) + 1) % 2 == 0 ? "left" : "right"));
                        enemyLastMove.add(System.nanoTime());
                        enemyType.add('1');

                    }
                    else if (line.charAt(i) == '2')
                    {
                        enemyX.add(i);
                        enemyY.add(j);
                        enemyDir.add(((int) ((Math.random() * 2) + 1) % 2 == 0 ? "up" : "down"));
                        enemyLastMove.add(System.nanoTime());
                        enemyType.add('2');
                    }
                }

                level.add(newRow);
                line = br.readLine();
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer = (int) (0.05 * level.size() * level.get(0).size()) * SECONDS_TO_NANO;
        cookiesNeeded = (int) (0.02 * level.size() * level.get(0).size());
        startTime = System.nanoTime();

        System.out.println(timer);

        spawnCookie();

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
        switch(state)
        {
            case MENU:
            {
                break;
            }
            case PLAYING:
            {
                currentTime = System.nanoTime();
                elaspedTime = timer - (currentTime - startTime);

                if (cookies >= cookiesNeeded)
                {
                    open = true;
                }

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
                    if (System.nanoTime() - enemyLastMove.get(i) < ENEMY_SPEED)
                    {
                        continue;
                    }

                    if (enemyType.get(i) == '1')
                    {
                        if (enemyDir.get(i).equals("left"))
                        {
                            if (level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('x') || level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('1') || level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('2') || level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('e'))
                            {
                                enemyDir.set(i, "right");
                            }
                            else
                            {
                                if (level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('c'))
                                {
                                    spawnCookie();
                                }

                                if (level.get(enemyY.get(i)).get(enemyX.get(i) - 1).equals('c'))
                                {
                                    state = LOSE;
                                }
                                else
                                {
                                    char self = level.get(enemyY.get(i)).get(enemyX.get(i));
                                    level.get(enemyY.get(i)).set(enemyX.get(i), '_');
                                    level.get(enemyY.get(i)).set(enemyX.get(i) - 1, self);
                                    enemyX.set(i, enemyX.get(i) - 1);
                                    enemyLastMove.set(i, System.nanoTime());
                                }  
                            }
                        }
                        else if (enemyDir.get(i).equals("right"))
                        {
                            if (level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('x') || level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('1') || level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('2') || level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('e'))
                            {
                                enemyDir.set(i, "left");
                            }
                            else
                            {
                                if (level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('c'))
                                {
                                    spawnCookie();
                                }

                                if (level.get(enemyY.get(i)).get(enemyX.get(i) + 1).equals('p'))
                                {
                                    state = LOSE;
                                }
                                else
                                {
                                    char self = level.get(enemyY.get(i)).get(enemyX.get(i));
                                    level.get(enemyY.get(i)).set(enemyX.get(i), '_');
                                    level.get(enemyY.get(i)).set(enemyX.get(i) + 1, self);
                                    enemyX.set(i, enemyX.get(i) + 1);
                                    enemyLastMove.set(i, System.nanoTime());
                                }
                            }
                        }
                    }
                    else if (enemyType.get(i) == '2')
                    {
                        if (enemyDir.get(i).equals("up"))
                        {
                            if (level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('x') || level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('1') || level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('2') || level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('e'))
                            {
                                enemyDir.set(i, "down");
                            }
                            else
                            {
                                if (level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('c'))
                                {
                                    spawnCookie();
                                }

                                if (level.get(enemyY.get(i) - 1).get(enemyX.get(i)).equals('p'))
                                {
                                    state = LOSE;
                                }
                                else
                                {
                                    char self = level.get(enemyY.get(i)).get(enemyX.get(i));
                                    level.get(enemyY.get(i)).set(enemyX.get(i), '_');
                                    level.get(enemyY.get(i) - 1).set(enemyX.get(i), self);
                                    enemyY.set(i, enemyY.get(i) - 1);
                                    enemyLastMove.set(i, System.nanoTime());
                                }
                            }
                        }
                        else if (enemyDir.get(i).equals("down"))
                        {
                            if (level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('x') || level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('1') || level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('2') || level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('e'))
                            {
                                enemyDir.set(i, "up");
                            }
                            else
                            {
                                if (level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('c'))
                                {
                                    spawnCookie();
                                }

                                if (level.get(enemyY.get(i) + 1).get(enemyX.get(i)).equals('p'))
                                {
                                    state = LOSE;
                                }
                                else
                                {
                                    char self = level.get(enemyY.get(i)).get(enemyX.get(i));
                                    level.get(enemyY.get(i)).set(enemyX.get(i), '_');
                                    level.get(enemyY.get(i) + 1).set(enemyX.get(i), self);
                                    enemyY.set(i, enemyY.get(i) + 1);
                                    enemyLastMove.set(i, System.nanoTime());
                                }
                            }
                        }
                    }
                }
                break;
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
                    if (!open)
                    {
                        g2d.drawImage(tiles[JAIL_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                    }
                    else
                    {
                        g2d.drawImage(tiles[CHECKERED_BLOCK], j * TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                    }
                }
                else if (character == 'c')
                {
                    g2d.drawImage(tiles[COOKIE], j*TILE_SIZE + xOffset, i * TILE_SIZE + yOffset, null);
                }
                j++;
            }
            j = 0;
            i++;
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.drawString(String.format("%s/%s",String.valueOf(cookies), String.valueOf(cookiesNeeded)), 1100, 65);

        g2d.setFont(new Font("Arial", Font.BOLD, 50));
        g2d.drawString(String.format("%.1f", ((double) elaspedTime / SECONDS_TO_NANO)), 400, 65);


        if (state == MENU)
        {
            g2d.setColor(new Color(0,0,0, 150));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));

            String startMessage = "Press ENTER to start";



            FontMetrics fm = g2d.getFontMetrics();
            int startMessageWidth = fm.stringWidth(startMessage);


            g2d.drawString(startMessage, (WIDTH - startMessageWidth)/2, HEIGHT/2);
        }

        if (state == LOSE)
        {
            g2d.setColor(new Color(0,0,0,150));
            g2d.fillRect(0,0,WIDTH,HEIGHT);
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 90));

            String loseMessage = "You Lose!";

            FontMetrics fm = g2d.getFontMetrics();
            int loseMessageWidth = fm.stringWidth(loseMessage);

            g2d.drawString(loseMessage, (WIDTH- loseMessageWidth)/2, HEIGHT/2 + 40);
        }
    }

    public static void movePlayer(int x, int y)
    {
        if (level.get(playerRow + y).get(playerCol + x) == 'x' || (level.get(playerRow + y).get(playerCol + x) == 'e' && !open))
        {
            return;
        }
        else
        {
            if (level.get(playerRow + y).get(playerCol + x) == 'c')
            {
                spawnCookie();
                cookies++;
            }

            if (level.get(playerRow + y).get(playerCol + x) == '1' || level.get(playerRow + y).get(playerCol + x) == '2')
            {
                state = LOSE;
            }

            if (state != LOSE)
            {
                level.get(playerRow).set(playerCol, '_');
                playerRow += y;
                playerCol += x;

                level.get(playerRow).set(playerCol, 'p');
            }  
        }
    }

    public static void spawnCookie()
    {
        int row;
        int col;

        do
        {
            row = (int) (Math.random() * level.size());
            col = (int) (Math.random() * level.get(row).size());
        } while (level.get(row).get(col) == 'x' || level.get(row).get(col) == 'p' || level.get(row).get(col) == '1' || level.get(row).get(col) == '2');

        level.get(row).set(col, 'c');
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

        if (e.getKeyCode() == KeyEvent.VK_ENTER && state == MENU)
        {
            state = PLAYING;
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