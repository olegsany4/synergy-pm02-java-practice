package autoschool.tools;

import autoschool.core.GamePanel;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Оффлайн-рендер кадров сцены приложения — без окна и без снятия экрана.
 *
 * <p>Нужен для двух честных вещей: (1) проверить, что сцена рисуется без ошибок
 * и изображения находятся; (2) получить кадры анимации для приложения к отчёту.
 * Это <b>не</b> скриншот экрана, а кадр, отрисованный самим приложением.</p>
 *
 * <p>Запуск: {@code java -cp bin autoschool.tools.SceneSnapshot <папка-вывода>}</p>
 */
public final class SceneSnapshot {

    private static final int FRAMES = 4;
    private static final long PAUSE_MS = 1200L;

    private SceneSnapshot() {
    }

    public static void main(String[] args) throws Exception {
        String outputDir = args.length > 0 ? args[0] : "render";
        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("Не удалось создать папку " + dir.getAbsolutePath());
        }

        final GamePanel panel = new GamePanel(null);
        SwingUtilities.invokeAndWait(() -> {
            panel.setSize(GamePanel.FIELD_WIDTH, GamePanel.FIELD_HEIGHT);
        });
        panel.start();

        for (int frame = 1; frame <= FRAMES; frame++) {
            Thread.sleep(PAUSE_MS);
            BufferedImage image = new BufferedImage(GamePanel.FIELD_WIDTH, GamePanel.FIELD_HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            final Graphics2D graphics = image.createGraphics();
            SwingUtilities.invokeAndWait(() -> panel.paint(graphics));
            graphics.dispose();

            File file = new File(dir, "scene-frame-" + frame + ".png");
            ImageIO.write(image, "png", file);
            System.out.println("Кадр сохранён: " + file.getPath());
        }
        System.exit(0);
    }
}
