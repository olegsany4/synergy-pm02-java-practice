package autoschool.core;

import autoschool.model.ExamRules;
import autoschool.model.FallingSign;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;

/**
 * Игровое поле тренажёра: анимированная сцена «автошкола».
 *
 * <p>Что анимируется: карточки категорий падают сверху вниз, учебный автомобиль
 * перемещается стрелками влево/вправо и слегка покачивается, светофор
 * переключает сигналы (на красный карточки падают чаще и быстрее).</p>
 */
public class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Размер поля. */
    public static final int FIELD_WIDTH = 1000;
    public static final int FIELD_HEIGHT = 620;

    private static final int CAR_WIDTH = 120;
    private static final int CAR_HEIGHT = 84;
    private static final int CAR_BOTTOM_MARGIN = 26;
    private static final double CAR_SPEED = 460.0;
    private static final double STEP_SECONDS = 1.0 / 60.0;
    private static final int TIMER_DELAY_MS = 16;
    private static final double LIGHT_PHASE_SECONDS = 2.0;
    private static final int CARD_SIZE = 78;
    private static final int SPAWN_LEFT_LIMIT = 100;
    private static final int SPAWN_RIGHT_MARGIN = 30;

    private final BufferedImage road = Assets.load("road.png");
    private final BufferedImage car = Assets.load("car.png");
    private final BufferedImage logo = Assets.load("logo.png");
    private final BufferedImage trafficGreen = Assets.load("traffic_green.png");
    private final BufferedImage trafficYellow = Assets.load("traffic_yellow.png");
    private final BufferedImage trafficRed = Assets.load("traffic_red.png");
    private final BufferedImage[] cards = {
            Assets.load("card_a.png"),
            Assets.load("card_b.png"),
            Assets.load("card_c.png"),
            Assets.load("card_d.png")
    };

    private final char[] categories = ExamRules.categories();
    private final List<FallingSign> signs = new ArrayList<>();
    private final Random random = new Random(20261103L);
    private final Timer timer;
    private final IntConsumer scoreListener;

    private double carLeft = (FIELD_WIDTH - CAR_WIDTH) / 2.0;
    private int carDirection = 0;
    private int score = 0;
    private int misses = 0;
    private int correctCatches = 0;
    private char target = 'B';
    private double secondsAlive = 0;
    private double spawnTimer = 0;
    private double lightTimer = 0;
    private int lightPhase = 0;
    private boolean paused = false;
    private boolean gameOver = false;

    public GamePanel(IntConsumer scoreListener) {
        this.scoreListener = scoreListener;
        setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        setBackground(new Color(38, 42, 48));
        setDoubleBuffered(true);
        bindKeys();
        this.timer = new Timer(TIMER_DELAY_MS, e -> onTick());
        this.timer.setCoalesce(true);
    }

    // ------------------------------------------------------------------ запуск

    /** Запустить анимацию. */
    public void start() {
        timer.start();
    }

    /** Сбросить результаты и начать заново. */
    public void restart() {
        signs.clear();
        score = 0;
        misses = 0;
        correctCatches = 0;
        target = 'B';
        secondsAlive = 0;
        spawnTimer = 0;
        lightTimer = 0;
        lightPhase = 0;
        carLeft = (FIELD_WIDTH - CAR_WIDTH) / 2.0;
        carDirection = 0;
        gameOver = false;
        paused = false;
        publishScore();
        if (!timer.isRunning()) {
            timer.start();
        }
        repaint();
    }

    // ------------------------------------------------------------- управление

    private void bindKeys() {
        InputMap input = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = getActionMap();

        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "car-left-on");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "car-right-on");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "car-off");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "car-off");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "car-left-on");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "car-right-on");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "car-off");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "car-off");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle-pause");
        input.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restart");

        actions.put("car-left-on", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carDirection = -1;
            }
        });
        actions.put("car-right-on", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carDirection = 1;
            }
        });
        actions.put("car-off", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carDirection = 0;
            }
        });
        actions.put("toggle-pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    paused = !paused;
                    repaint();
                }
            }
        });
        actions.put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restart();
            }
        });
    }

    // -------------------------------------------------------------- один кадр

    private void onTick() {
        if (!paused && !gameOver) {
            update(STEP_SECONDS);
        }
        repaint();
    }

    private void update(double deltaSeconds) {
        secondsAlive += deltaSeconds;

        lightTimer += deltaSeconds;
        if (lightTimer >= LIGHT_PHASE_SECONDS) {
            lightTimer = 0;
            lightPhase = (lightPhase + 1) % 3;
        }
        boolean redLight = lightPhase == 2;

        moveCar(deltaSeconds);
        spawnSigns(deltaSeconds, redLight);
        moveSigns(deltaSeconds);
    }

    private void moveCar(double deltaSeconds) {
        if (carDirection != 0) {
            carLeft += carDirection * CAR_SPEED * deltaSeconds;
            if (carLeft < 0) {
                carLeft = 0;
            }
            double maxLeft = FIELD_WIDTH - CAR_WIDTH;
            if (carLeft > maxLeft) {
                carLeft = maxLeft;
            }
        }
    }

    private void spawnSigns(double deltaSeconds, boolean redLight) {
        spawnTimer += deltaSeconds;
        if (spawnTimer < ExamRules.spawnInterval(redLight)) {
            return;
        }
        spawnTimer = 0;
        char category = categories[random.nextInt(categories.length)];
        double usableWidth = FIELD_WIDTH - CARD_SIZE - SPAWN_LEFT_LIMIT - SPAWN_RIGHT_MARGIN;
        double x = SPAWN_LEFT_LIMIT + random.nextDouble() * usableWidth;
        double speed = ExamRules.signSpeed(score, redLight);
        signs.add(new FallingSign(category, x, -CARD_SIZE, speed, CARD_SIZE));
    }

    private void moveSigns(double deltaSeconds) {
        double catchLine = carTop() + ExamRules.CATCH_LINE_OFFSET;
        List<FallingSign> caught = new ArrayList<>();
        List<FallingSign> missed = new ArrayList<>();

        for (FallingSign sign : signs) {
            sign.fall(deltaSeconds);
            if (sign.isCaughtBy(carLeft, carLeft + CAR_WIDTH, catchLine)) {
                caught.add(sign);
            } else if (sign.isOutOfField(FIELD_HEIGHT)) {
                missed.add(sign);
            }
        }

        for (FallingSign sign : caught) {
            int points = ExamRules.pointsFor(target, sign.getCategory());
            score += points;
            if (points > 0) {
                correctCatches++;
                target = ExamRules.nextTarget(target, correctCatches);
            } else {
                misses++;
            }
            signs.remove(sign);
        }
        for (FallingSign sign : missed) {
            misses++;
            signs.remove(sign);
        }
        if (!caught.isEmpty() || !missed.isEmpty()) {
            publishScore();
        }
        if (ExamRules.isExamFailed(misses)) {
            gameOver = true;
        }
    }

    private void publishScore() {
        if (scoreListener != null) {
            scoreListener.accept(score);
        }
    }

    // -------------------------------------------------------------- рисование

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawField(g);
        drawFallingCards(g);
        drawCar(g);
        drawTrafficLight(g);
        drawLogo(g);
        drawHud(g);

        if (paused) {
            drawBanner(g, "ПАУЗА", "пробел — продолжить, R — начать заново", new Color(20, 24, 30, 210));
        }
        if (gameOver) {
            drawBanner(g, "ЭКЗАМЕН НЕ СДАН",
                    "промахов: " + misses + " из " + ExamRules.MAX_MISSES
                            + ", очки: " + score + ". R — попробовать снова",
                    new Color(120, 26, 26, 220));
        }
        g.dispose();
    }

    private void drawField(Graphics2D g) {
        if (road != null) {
            g.drawImage(road, 0, 0, FIELD_WIDTH, FIELD_HEIGHT, null);
        } else {
            g.setColor(new Color(86, 88, 92));
            g.fillRect(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
        }
    }

    private void drawFallingCards(Graphics2D g) {
        for (FallingSign sign : signs) {
            BufferedImage image = imageFor(sign.getCategory());
            if (image != null) {
                g.drawImage(image, (int) Math.round(sign.getX()), (int) Math.round(sign.getY()),
                        sign.getSize(), sign.getSize(), null);
            }
        }
    }

    private void drawCar(Graphics2D g) {
        if (car == null) {
            return;
        }
        // лёгкое покачивание кузова — чтобы автомобиль «жил» даже без нажатых клавиш
        double bob = Math.sin(secondsAlive * 6.0) * 2.0;
        int y = (int) Math.round(carTop() + bob);
        g.drawImage(car, (int) Math.round(carLeft), y, CAR_WIDTH, CAR_HEIGHT, null);
    }

    /** Верхняя граница автомобиля: машина стоит на дороге, не упираясь в нижний край поля. */
    private int carTop() {
        return FIELD_HEIGHT - CAR_HEIGHT - CAR_BOTTOM_MARGIN;
    }

    private void drawTrafficLight(Graphics2D g) {
        BufferedImage image;
        if (lightPhase == 0) {
            image = trafficGreen;
        } else if (lightPhase == 1) {
            image = trafficYellow;
        } else {
            image = trafficRed;
        }
        if (image == null) {
            return;
        }
        // жёлтый мигает — это тоже анимация
        boolean visible = lightPhase != 1 || ((int) (secondsAlive * 4) % 2 == 0);
        if (visible) {
            g.drawImage(image, 24, 22, 60, 160, null);
        }
    }

    private void drawLogo(Graphics2D g) {
        if (logo != null) {
            g.drawImage(logo, FIELD_WIDTH - 280, 20, 260, 92, null);
        }
    }

    private void drawHud(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(100, 22, 340, 116, 18, 18);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        g.drawString("Очки: " + score, 118, 58);
        g.drawString("Промахи: " + misses + " / " + ExamRules.MAX_MISSES, 118, 90);
        g.setColor(new Color(255, 224, 130));
        g.drawString("Нужна категория: " + target, 118, 122);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(255, 255, 255, 210));
        g.drawString("← → (или A / D) — перемещать автомобиль · пробел — пауза · R — заново",
                24, FIELD_HEIGHT - 12);
    }

    private void drawBanner(Graphics2D g, String title, String subtitle, Color background) {
        g.setColor(background);
        g.fillRoundRect(140, 220, FIELD_WIDTH - 280, 170, 24, 24);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 46));
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (FIELD_WIDTH - titleWidth) / 2, 292);
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        int subtitleWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (FIELD_WIDTH - subtitleWidth) / 2, 340);
    }

    private BufferedImage imageFor(char category) {
        int index = category - 'A';
        if (index < 0 || index >= cards.length) {
            return null;
        }
        return cards[index];
    }
}
