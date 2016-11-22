package Arknoid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Board extends JPanel implements Commons {
    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Paddle paddle;
    private Brick bricks[];
    private Brick1 bricks1[];
    private boolean ingame = true;
    private int stage = 0;
    private int n,count = 0;



    public Board() {
        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);

        bricks = new Brick[N_OF_BRICKS];
        bricks1 = new Brick1[N_OF_BRICKS];
        setDoubleBuffered(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), DELAY, PERIOD);

    }




    private void nextStage() {


        gameInit2();
    }

    @Override
    public void addNotify() {

        super.addNotify();
        gameInit();
    }

    private void gameInit() {

        ball = new Ball();
        paddle = new Paddle();

        int k = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[k] = new Brick(j * 100 + 100, i * 40 + 50, 3);
                k++;
            }
        }
    }

    private void gameInit2() {


        ball = new Ball();
        paddle = new Paddle();
        count++;
        int k = 0;
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            n = rand.nextInt(count)+1;


            for (int j = 0; j < 6; j++) {

                bricks[k] = new Brick(j * 100 + 100, i * 40 + 50, k%n);
                k++;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        if (ingame) {
            drawObjects(g2d);
        }else {
            gameFinished();
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics2D g2d) {

            g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                    ball.getWidth(), ball.getHeight(), this);
            g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
                    paddle.getWidth(), paddle.getHeight(), this);

            for (int i = 0; i < N_OF_BRICKS; i++) {
                if (!bricks[i].isDestroyed()) {
                    g2d.drawImage(bricks[i].getImage(), bricks[i].getX(),
                            bricks[i].getY(), bricks[i].getWidth(),
                            bricks[i].getHeight(), this);
                }
            }
    }


    private void newGame() {
        Akanoid game = new Akanoid();
        game.setVisible(true);
        ball.move();
        paddle.move();
        checkCollision();
        repaint();
    }

    private void gameFinished() {
        JOptionPane.showMessageDialog(null, message);
        //message = "New Game";
        //JOptionPane.showMessageDialog(null, message);
        //newGame();

    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            paddle.keyPressed(e);
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            ball.move();
            paddle.move();
            checkCollision();
            repaint();
        }
    }

    private void stopGame() {

        ingame = false;
        timer.cancel();
    }


    private void checkCollision() {


        if (ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {
            stopGame();
        }

        for (int i = 0, j = 0; i < N_OF_BRICKS; i++) {

            if (bricks[i].isDestroyed()) {
                j++;
            }

            if (j == N_OF_BRICKS) {
                stage++;
                if (stage<=2){
                    JOptionPane.showMessageDialog(null, "Next Stage");
                    nextStage();
                }else {
                    message = "Victory";
                    stopGame();

                }

            }
        }

        if ((ball.getRect()).intersects(paddle.getRect())) {

            int paddleLPos = (int) paddle.getRect().getMinX();
            int ballLPos = (int) ball.getRect().getMinX();

            int first = paddleLPos + 8;
            int second = paddleLPos + 16;
            int third = paddleLPos + 24;
            int fourth = paddleLPos + 32;

            if (ballLPos < first) {
                ball.setXDir(-1);
                ball.setYDir(-1);
            }

            if (ballLPos >= first && ballLPos < second) {
                ball.setXDir(-1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos >= second && ballLPos < third) {
                ball.setXDir(0);
                ball.setYDir(-1);
            }

            if (ballLPos >= third && ballLPos < fourth) {
                ball.setXDir(1);
                ball.setYDir(-1 * ball.getYDir());
            }

            if (ballLPos > fourth) {
                ball.setXDir(1);
                ball.setYDir(-1);
            }
        }
            for (int i = 0; i < N_OF_BRICKS; i++) {

                if ((ball.getRect()).intersects(bricks[i].getRect())) {

                    int ballLeft = (int) ball.getRect().getMinX();
                    int ballHeight = (int) ball.getRect().getHeight();
                    int ballWidth = (int) ball.getRect().getWidth();
                    int ballTop = (int) ball.getRect().getMinY();

                    Point pointRight = new Point(ballLeft + ballWidth + 1, ballTop);
                    Point pointLeft = new Point(ballLeft - 1, ballTop);
                    Point pointTop = new Point(ballLeft, ballTop - 1);
                    Point pointBottom = new Point(ballLeft, ballTop + ballHeight + 1);

                    if (!bricks[i].isDestroyed()) {
                        if (bricks[i].getRect().contains(pointRight)) {
                            ball.setXDir(-1);
                        } else if (bricks[i].getRect().contains(pointLeft)) {
                            ball.setXDir(1);
                        }

                        if (bricks[i].getRect().contains(pointTop)) {
                            ball.setYDir(1);
                        } else if (bricks[i].getRect().contains(pointBottom)) {
                            ball.setYDir(-1);
                        }

                        bricks[i].setDestroyed();
                    }
                }
            }
        }
    }