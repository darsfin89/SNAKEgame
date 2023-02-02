import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JPanel;
import javax.sound.sampled.*;
public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 650;
    static final int UNIT_SIZE = 25;
    static final int SCORE_BOARD = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean isPaused = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT/4));
        scorePanel.setLayout(new GridLayout(1, 1, 10, 1));
        scorePanel.setBackground(Color.WHITE);
        scorePanel.setVisible(true);
    }


    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // this is a grid that doesn't need to be here
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, SCORE_BOARD, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            //creates the apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            //creates the head
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(52, 145, 18));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                //eyes
                    g.setColor(Color.BLACK);
                    g.fillRect(x[i]+4, y[i], UNIT_SIZE/6, UNIT_SIZE/2);
                    g.setColor(Color.BLACK);
                    g.fillRect(x[i]+19, y[i], UNIT_SIZE/6, UNIT_SIZE/2);
                } else {
                    //creates the body
                    g.setColor(new Color(151, 220, 29));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    g.setColor(new Color(4, 105, 8));
                    g.fillRect(x[i], y[i], UNIT_SIZE-12, UNIT_SIZE-2);
                    g.setColor(new Color(44, 130, 2));
                    g.fillRect(x[i], y[i], UNIT_SIZE-10, UNIT_SIZE-12);
                    g.setColor(new Color(13, 160, 50));
                    g.fillRect(x[i], y[i], UNIT_SIZE/6, UNIT_SIZE);
                    g.fillRect(x[i]+19, y[i], UNIT_SIZE/6, UNIT_SIZE);
                }

            }
            g.setColor(new Color(222, 233, 53));
            g.setFont(new Font("ariel", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

            } else {
            gameOver(g);
        }
    }

    public void newApple() {
        //moves the apple to a random spot each time the game starts over or an apple is eaten
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) ((SCREEN_HEIGHT - SCORE_BOARD) / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        //if snake eats apple, body gets longer and score increases
        if ((x[0] == appleX) && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }


    public void checkCollisions() {
        //checks if head hits body, game over
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && y[0] == y[i]) {
                running = false;
            }
        }
        //check if head touches left border it wraps around
        if (x[0] < 0) {
            x[0] = SCREEN_WIDTH;
        }
        // check if head touches right border it wraps around
        if (x[0] > SCREEN_WIDTH) {
            x[0] = 0;
        }
        //check if head touches top border it wraps around
        if (y[0] < 0 + SCORE_BOARD) {
            y[0] = SCREEN_HEIGHT;
        }
        //check if head touches bottom border it wraps around
        if (y[0] > SCREEN_HEIGHT) {
            y[0] = 0 + SCORE_BOARD;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        //display score
        g.setColor(new Color(222, 233, 63));
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        //press space bar to start again
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Space Bar to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press Space Bar to Restart")) / 2, SCREEN_HEIGHT - UNIT_SIZE * 10);
    }


    public void start(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 100));
        FontMetrics metrics5 = getFontMetrics(g.getFont());
        g.drawString("SNAKE", (SCREEN_WIDTH - metrics5.stringWidth("SNAKE")) / 2, g.getFont().getSize());
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics6 = getFontMetrics(g.getFont());
        g.drawString("How many apples can you eat?", (SCREEN_WIDTH - metrics6.stringWidth("How many apples can you eat?")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press 'S' to Start", (SCREEN_WIDTH - metrics3.stringWidth("Press 'S' to Start")) / 2, SCREEN_HEIGHT - UNIT_SIZE * 10);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if(!running && e.getKeyCode() == KeyEvent.VK_SPACE) {
                        applesEaten = 0;
                        bodyParts = 3;
                        startGame();
                    }
                    break;
                case KeyEvent.VK_P:
                    if(running && e.getKeyCode() == KeyEvent.VK_P) {
                       timer.stop();
                       isPaused = true;

                    }
                    break;
                case KeyEvent.VK_S:
                    if(isPaused && e.getKeyCode() == KeyEvent.VK_S) {
                        startGame();
                    }
            }
        }
    }
}
